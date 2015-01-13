package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle blood lust.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter MOVE_STACK for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepBloodLust extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnFailure;
	
	public StepBloodLust(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.BLOOD_LUST;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  			  // optional
  				case GOTO_LABEL_ON_FAILURE:
  					fGotoLabelOnFailure = (String) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
  	}
  }

	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
  @Override
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
      executeStep();
    }
    return commandStatus;
  }
	
  private void executeStep() {
    ActionStatus status = ActionStatus.SUCCESS;
    Game game = getGameState().getGame();
    if (!game.getTurnMode().checkNegatraits()) {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    	return;
    }
    ActingPlayer actingPlayer = game.getActingPlayer();
	  boolean doRoll = true;
	  if (ReRolledAction.BLOOD_LUST == getReRolledAction()) {
	    if ((getReRollSource() == null) || !UtilServerReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
	      doRoll = false;
	      status = ActionStatus.FAILURE;
	      actingPlayer.setSufferingBloodLust(true);
	    }
	  } else {
	    doRoll = UtilCards.hasUnusedSkill(game, actingPlayer, Skill.BLOOD_LUST);
	  }
    if (doRoll) {
      int roll = getGameState().getDiceRoller().rollSkill();
      int minimumRoll = DiceInterpreter.getInstance().minimumRollBloodLust();
      boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
      actingPlayer.markSkillUsed(Skill.BLOOD_LUST);
      if (!successful) {
        status = ActionStatus.FAILURE;
        if ((ReRolledAction.BLOOD_LUST != getReRolledAction()) && UtilServerReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.BLOOD_LUST, minimumRoll, false)) {
          status = ActionStatus.WAITING_FOR_RE_ROLL;
        } else {
          actingPlayer.setSufferingBloodLust(true);
        }
      }
      boolean reRolled = ((ReRolledAction.BLOOD_LUST == getReRolledAction()) && (getReRollSource() != null));
      getResult().addReport(new ReportSkillRoll(ReportId.BLOOD_LUST_ROLL, actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled));
    }
    if (status == ActionStatus.SUCCESS) {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
    if (status == ActionStatus.FAILURE) {
    	publishParameter(new StepParameter(StepParameterKey.MOVE_STACK, null));
    	if (StringTool.isProvided(fGotoLabelOnFailure)) {
    		getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
    	} else {
      	getResult().setNextAction(StepAction.NEXT_STEP);    		
    	}
    }
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    return jsonObject;
  }
  
  @Override
  public StepBloodLust initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    return this;
  }
	
}
