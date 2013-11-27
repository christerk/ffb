package com.balancedbytes.games.ffb.server.step.action.pass;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogSkillUseParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportPassRoll;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in the pass sequence to handle skill HAIL_MARY_PASS.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter PASS_FUMBLE for all steps on the stack.
 * 
 * @author Kalimar
 */
public final class StepHailMaryPass extends AbstractStepWithReRoll {
	
  private String fGotoLabelOnFailure;
  private boolean fPassFumble;
  private boolean fPassSkillUsed;
	
	public StepHailMaryPass(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.HAIL_MARY_PASS;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // mandatory
  				case GOTO_LABEL_ON_FAILURE:
  					fGotoLabelOnFailure = (String) parameter.getValue();
  					break;
					default:
						break;
  			}  			
  		}
  	}
  	if (!StringTool.isProvided(fGotoLabelOnFailure)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
  	}
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
    if (game.getThrower() == null) {
    	return;
    }
    if (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()) {
    	game.getFieldModel().setBombMoving(true);
    } else {
    	game.getFieldModel().setBallMoving(true);
    }
    boolean doRoll = true;
  	boolean doNextStep = false;
    if (ReRolledAction.PASS == getReRolledAction()) {
      if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), game.getThrower())) {
      	doRoll = false;
      	doNextStep = true;
      }
    }
    if (doRoll) {
      int roll = getGameState().getDiceRoller().rollSkill();
      fPassFumble = (roll == 1);
      boolean reRolled = ((getReRolledAction() == ReRolledAction.PASS) && (getReRollSource() != null));
      getResult().addReport(new ReportPassRoll(game.getThrowerId(), fPassFumble, roll, reRolled, (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction())));
    	doNextStep = true;
      if (fPassFumble) {
        if (getReRolledAction() != ReRolledAction.PASS) {
          setReRolledAction(ReRolledAction.PASS);
          if (UtilCards.hasSkill(game, game.getThrower(), Skill.PASS) && !fPassSkillUsed) {
          	doNextStep = false;
          	fPassSkillUsed = true;
            UtilDialog.showDialog(getGameState(), new DialogSkillUseParameter(game.getThrowerId(), Skill.PASS, 2));
          } else {
            if (UtilReRoll.askForReRollIfAvailable(getGameState(), game.getThrower(), ReRolledAction.PASS, 2, false)) {
            	doNextStep = false;
            }
          }
        }
      }
      if (doNextStep) {
      	publishParameter(new StepParameter(StepParameterKey.PASS_FUMBLE, fPassFumble));
      	if (fPassFumble) {
      		if (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()) {
	      		game.getFieldModel().setBombCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
      		} else {
	      		game.getFieldModel().setBallCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
	          publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
      		}
          getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
        } else {
      		if (PlayerAction.HAIL_MARY_BOMB == game.getThrowerAction()) {
	      		game.getFieldModel().setBombCoordinate(game.getFieldModel().getPlayerCoordinate(game.getThrower()));
      			game.getFieldModel().setBombMoving(false);
      		} else {
      			game.getFieldModel().setBallCoordinate(game.getPassCoordinate());
      		}
          getResult().setNextAction(StepAction.NEXT_STEP);
        }
      }
    }
  }
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnFailure);
  	pByteList.addBoolean(fPassFumble);
  	pByteList.addBoolean(fPassSkillUsed);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnFailure = pByteArray.getString();
  	fPassFumble = pByteArray.getBoolean();
  	fPassSkillUsed = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = toJsonValueTemp();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    IServerJsonOption.PASS_FUMBLE.addTo(jsonObject, fPassFumble);
    IServerJsonOption.PASS_SKILL_USED.addTo(jsonObject, fPassSkillUsed);
    return jsonObject;
  }
  
  public StepHailMaryPass initFrom(JsonValue pJsonValue) {
    initFromTemp(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(jsonObject);
    fPassFumble = IServerJsonOption.PASS_FUMBLE.getFrom(jsonObject);
    fPassSkillUsed = IServerJsonOption.PASS_SKILL_USED.getFrom(jsonObject);
    return this;
  }
  
}
