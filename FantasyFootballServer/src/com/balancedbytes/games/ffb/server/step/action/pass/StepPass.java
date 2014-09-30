package com.balancedbytes.games.ffb.server.step.action.pass;

import java.util.Set;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassModifierFactory;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportPassRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in the pass sequence to handle passing the ball.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_END.
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_MISSED_PASS.
 * 
 * Expects stepParameter CATCHER_ID to be set by a preceding step.
 * 
 * Sets stepParameter CATCHER_ID for all steps on the stack.
 * Sets stepParameter PASS_ACCURATE for all steps on the stack.
 * Sets stepParameter PASS_FUMBLE for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepPass extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnEnd;
	private String fGotoLabelOnMissedPass;
	private String fCatcherId;
	private boolean fSuccessful;
	private boolean fHoldingSafeThrow;
	private boolean fPassFumble;
	private boolean fPassSkillUsed;
	
	public StepPass(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.PASS;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // mandatory
  				case GOTO_LABEL_ON_END:
  					fGotoLabelOnEnd = (String) parameter.getValue();
  					break;
  			  // mandatory
  				case GOTO_LABEL_ON_MISSED_PASS:
  					fGotoLabelOnMissedPass = (String) parameter.getValue();
  					break;
  				default:
  					break;
  			}
  		}
  	}
  	if (!StringTool.isProvided(fGotoLabelOnEnd)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
  	}
  	if (!StringTool.isProvided(fGotoLabelOnMissedPass)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_MISSED_PASS + " is not initialized.");
  	}
  }
  
  @Override
  public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
	  	switch (pParameter.getKey()) {
				case CATCHER_ID:
					fCatcherId = (String) pParameter.getValue();
					return true;
				default:
					break;
	  	}
		}
		return false;
  }

	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
  @Override
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      switch (pReceivedCommand.getId()) {
        case CLIENT_USE_SKILL:
          ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
          if (Skill.PASS == useSkillCommand.getSkill()) {
            setReRolledAction(ReRolledAction.PASS);
            setReRollSource(useSkillCommand.isSkillUsed() ? ReRollSource.PASS : null);
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        default:
          break;
      }
    }
    if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
      executeStep();
    }
    return commandStatus;
  }

  private void executeStep() {
    Game game = getGameState().getGame();
    if ((game.getThrower() == null) || (game.getThrowerAction() == null)) {
    	return;
    }
    if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
    	game.getFieldModel().setBombMoving(true);
    } else {
    	game.getFieldModel().setBallMoving(true);
    }
    if (ReRolledAction.PASS == getReRolledAction()) {
      if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), game.getThrower())) {
    	  handleFailedPass();
        return;
      }
    }
    FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
    PassingDistance passingDistance = UtilPassing.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), false);
    Set<PassModifier> passModifiers = new PassModifierFactory().findPassModifiers(game, game.getThrower(), passingDistance, false);
    int minimumRoll = DiceInterpreter.getInstance().minimumRollPass(game.getThrower(), passingDistance, passModifiers);
    int roll = getGameState().getDiceRoller().rollSkill();
    if (roll == 6) {
    	fSuccessful = true;
    	fPassFumble = false;
    	fHoldingSafeThrow = false;
    } else if (roll == 1) {
    	fSuccessful = false;
    	fPassFumble = true;
    	fHoldingSafeThrow = false;
    } else {
    	fPassFumble = DiceInterpreter.getInstance().isPassFumble(roll, game.getThrower(), passingDistance, passModifiers); 
			if (fPassFumble) {
				fSuccessful = false;
				fHoldingSafeThrow = (UtilCards.hasSkill(game, game.getThrower(), Skill.SAFE_THROW) && (PlayerAction.THROW_BOMB != game.getThrowerAction()));
			} else {
				fSuccessful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
				fHoldingSafeThrow = false;
			}
		}
    PassModifier[] passModifierArray = new PassModifierFactory().toArray(passModifiers);
    boolean reRolled = ((getReRolledAction() == ReRolledAction.PASS) && (getReRollSource() != null));
    getResult().addReport(new ReportPassRoll(game.getThrowerId(), fSuccessful, roll, minimumRoll, reRolled, passModifierArray, passingDistance, fPassFumble, fHoldingSafeThrow, (PlayerAction.THROW_BOMB == game.getThrowerAction())));
    if (fSuccessful) {
      game.getFieldModel().setRangeRuler(null);
    	publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, fPassFumble));
      FieldCoordinate startCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
      if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
      	getResult().setAnimation(new Animation(AnimationType.THROW_BOMB, startCoordinate, game.getPassCoordinate(), null));
      } else {
      	getResult().setAnimation(new Animation(AnimationType.PASS, startCoordinate, game.getPassCoordinate(), null));
      }
      UtilServerGame.syncGameModel(this);
      Player catcher = game.getPlayerById(fCatcherId);
      PlayerState catcherState = game.getFieldModel().getPlayerState(catcher);
      if ((catcher == null) || (catcherState == null) || !catcherState.hasTacklezones()) {
        if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
        	game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
        	publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_BOMB));
        } else {
        	game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
        	publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_MISSED_PASS));
        }
       	getResult().setNextAction(StepAction.NEXT_STEP);
      } else {
        if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
        	game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
	      	publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_ACCURATE_BOMB));
        } else {
        	game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
	      	publishParameter(new StepParameter(StepParameterKey.PASS_ACCURATE, true));
	      	publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_ACCURATE_PASS));
        }
       	getResult().setNextAction(StepAction.NEXT_STEP);
      }
    } else {
    	boolean doNextStep = true;
      if (getReRolledAction() != ReRolledAction.PASS) {
        setReRolledAction(ReRolledAction.PASS);
        if (UtilCards.hasSkill(game, game.getThrower(), Skill.PASS) && !fPassSkillUsed) {
        	doNextStep = false;
        	fPassSkillUsed = true;
          UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getThrowerId(), Skill.PASS, minimumRoll));
        } else {
          if (UtilServerReRoll.askForReRollIfAvailable(getGameState(), game.getThrower(), ReRolledAction.PASS, minimumRoll, fPassFumble)) {
          	doNextStep = false;
          }
        }
      }
      if (doNextStep) {
      	handleFailedPass();
      }
    }
  }
  
  private void handleFailedPass() {
  	Game game = getGameState().getGame();
    game.getFieldModel().setRangeRuler(null);
    FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
  	publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, fPassFumble));
	  if (fHoldingSafeThrow){
		  game.getFieldModel().setBallCoordinate(throwerCoordinate);
		  game.getFieldModel().setBallMoving(false);
     	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnEnd);     	
	  } else if (fPassFumble) {
      if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
	      game.getFieldModel().setBombCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
      } else {
	      game.getFieldModel().setBallCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
	    	publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
      }
    	publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, null));
     	getResult().setNextAction(StepAction.NEXT_STEP);
    } else {
      if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
      	game.getFieldModel().setBombCoordinate(game.getPassCoordinate());
      } else {
      	game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
      }
    	publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, null));
     	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnMissedPass);
    }
  }
  
  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnEnd = pByteArray.getString();
  	fGotoLabelOnMissedPass = pByteArray.getString();
  	fCatcherId = pByteArray.getString();
  	fSuccessful = pByteArray.getBoolean();
  	fHoldingSafeThrow = pByteArray.getBoolean();
  	fPassFumble = pByteArray.getBoolean();
  	fPassSkillUsed = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
    IServerJsonOption.GOTO_LABEL_ON_MISSED_PASS.addTo(jsonObject, fGotoLabelOnMissedPass);
    IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
    IServerJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    IServerJsonOption.HOLDING_SAFE_THROW.addTo(jsonObject, fHoldingSafeThrow);
    IServerJsonOption.PASS_FUMBLE.addTo(jsonObject, fPassFumble);
    IServerJsonOption.PASS_SKILL_USED.addTo(jsonObject, fPassSkillUsed);
    return jsonObject;
  }
  
  @Override
  public StepPass initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(jsonObject);
    fGotoLabelOnMissedPass = IServerJsonOption.GOTO_LABEL_ON_MISSED_PASS.getFrom(jsonObject);
    fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(jsonObject);
    fSuccessful = IServerJsonOption.SUCCESSFUL.getFrom(jsonObject);
    fHoldingSafeThrow = IServerJsonOption.HOLDING_SAFE_THROW.getFrom(jsonObject);
    fPassFumble = IServerJsonOption.PASS_FUMBLE.getFrom(jsonObject);
    fPassSkillUsed = IServerJsonOption.PASS_SKILL_USED.getFrom(jsonObject);
    return this;
  }
  
}
