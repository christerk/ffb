package com.balancedbytes.games.ffb.server.step.action.pass;

import java.util.Set;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportPassRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPassing;

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
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
	      case CLIENT_USE_SKILL:
	        ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pNetCommand;
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
      if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), game.getThrower())) {
    	  handleFailedPass();
        return;
      }
    }
    FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
    PassingDistance passingDistance = UtilPassing.findPassingDistance(game, throwerCoordinate, game.getPassCoordinate(), false);
    Set<PassModifier> passModifiers = PassModifier.findPassModifiers(game, game.getThrower(), passingDistance, false);
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
    PassModifier[] passModifierArray = PassModifier.toArray(passModifiers);
    boolean reRolled = ((getReRolledAction() == ReRolledAction.PASS) && (getReRollSource() != null));
    getResult().addReport(new ReportPassRoll(game.getThrowerId(), fSuccessful, fPassFumble, roll, minimumRoll, passingDistance, passModifierArray, reRolled, fHoldingSafeThrow, (PlayerAction.THROW_BOMB == game.getThrowerAction())));
    if (fSuccessful) {
      game.getFieldModel().setRangeRuler(null);
    	publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, fPassFumble));
      FieldCoordinate startCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
      if (PlayerAction.THROW_BOMB == game.getThrowerAction()) {
      	getResult().setAnimation(new Animation(AnimationType.THROW_BOMB, startCoordinate, game.getPassCoordinate(), null));
      } else {
      	getResult().setAnimation(new Animation(AnimationType.PASS, startCoordinate, game.getPassCoordinate(), null));
      }
      UtilGame.syncGameModel(this);
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
          UtilDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getThrowerId(), Skill.PASS, minimumRoll));
        } else {
          if (UtilReRoll.askForReRollIfAvailable(getGameState(), game.getThrower(), ReRolledAction.PASS, minimumRoll, fPassFumble)) {
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
  
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnEnd);
  	pByteList.addString(fGotoLabelOnMissedPass);
  	pByteList.addString(fCatcherId);
  	pByteList.addBoolean(fSuccessful);
  	pByteList.addBoolean(fHoldingSafeThrow);
  	pByteList.addBoolean(fPassFumble);
  	pByteList.addBoolean(fPassSkillUsed);
  }
  
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
  
}
