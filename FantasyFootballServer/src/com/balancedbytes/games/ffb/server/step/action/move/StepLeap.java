package com.balancedbytes.games.ffb.server.step.action.move;

import java.util.Set;

import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.LeapModifier;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
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
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in move sequence to handle skill LEAP.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter INJURY_TYPE for all steps on the stack.
 *  
 * @author Kalimar
 */
public class StepLeap extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnFailure;
	
	public StepLeap(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.LEAP;
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
  	if (fGotoLabelOnFailure == null) {
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}
	
  private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    boolean doLeap = (actingPlayer.isLeaping() && UtilCards.hasUnusedSkill(game, actingPlayer, Skill.LEAP));
    if (doLeap) {
      if (ReRolledAction.LEAP == getReRolledAction()) {
        if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
        	publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, InjuryType.DROP_LEAP));
        	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
          doLeap = false;
        }
      }
      if (doLeap) {
        switch (leap()) {
          case SUCCESS:
            actingPlayer.setLeaping(false);
            actingPlayer.markSkillUsed(Skill.LEAP);
          	getResult().setNextAction(StepAction.NEXT_STEP);
            break;
          case FAILURE:
            actingPlayer.setLeaping(false);
            actingPlayer.markSkillUsed(Skill.LEAP);
          	publishParameter(new StepParameter(StepParameterKey.INJURY_TYPE, InjuryType.DROP_LEAP));
          	getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
            break;
          default:
          	break;
        }
      }
    } else {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  private ActionStatus leap() {
    ActionStatus status = null;
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    Set<LeapModifier> leapModifiers = LeapModifier.findLeapModifiers(game);
    int minimumRoll = DiceInterpreter.getInstance().minimumRollLeap(actingPlayer.getPlayer(), leapModifiers);
    int roll = getGameState().getDiceRoller().rollSkill();
    boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
    LeapModifier[] leapModifierArray = LeapModifier.toArray(leapModifiers);
    boolean reRolled = ((getReRolledAction() == ReRolledAction.LEAP) && (getReRollSource() != null));
    getResult().addReport(new ReportSkillRoll(ReportId.LEAP_ROLL, actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled, leapModifierArray));
    if (successful) {
      status = ActionStatus.SUCCESS;
    } else {
      status = ActionStatus.FAILURE;
      if (getReRolledAction() != ReRolledAction.LEAP) {
        setReRolledAction(ReRolledAction.LEAP);
        if (UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.LEAP, minimumRoll, false)) {
          status = ActionStatus.WAITING_FOR_RE_ROLL;
        }
      }
    }
    return status;
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }

  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnFailure);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnFailure = pByteArray.getString();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    return jsonObject;
  }
  
  @Override
  public StepLeap initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    return this;
  }
  	
}
