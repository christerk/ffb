package com.balancedbytes.games.ffb.server.step.action.foul;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStepWithReRoll;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilInjury;
import com.balancedbytes.games.ffb.server.util.UtilReRoll;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in foul sequence to handle skill CHAINSAW.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepFoulChainsaw extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnFailure;
	
	public StepFoulChainsaw(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.FOUL_CHAINSAW;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
      executeStep();
    }
    return commandStatus;
  }
	
  private void executeStep() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (UtilCards.hasSkill(game, actingPlayer, Skill.CHAINSAW)) {
      boolean dropChainsawPlayer = false;
      if (ReRolledAction.CHAINSAW == getReRolledAction()) {
        if ((getReRollSource() == null) || !UtilReRoll.useReRoll(this, getReRollSource(), actingPlayer.getPlayer())) {
          dropChainsawPlayer = true;
        }
      }
      if (!dropChainsawPlayer) {
        boolean reRolled = ((getReRolledAction() == ReRolledAction.CHAINSAW) && (getReRollSource() != null));
        if (!reRolled) {
          getResult().setSound(Sound.CHAINSAW);
        }
        int roll = getGameState().getDiceRoller().rollChainsaw();
        int minimumRoll = DiceInterpreter.getInstance().minimumRollChainsaw();
        boolean successful = (roll >= minimumRoll);
        getResult().addReport(new ReportSkillRoll(ReportId.CHAINSAW_ROLL, actingPlayer.getPlayerId(), successful, roll, minimumRoll, reRolled));
        if (successful) {
        	getResult().setNextAction(StepAction.NEXT_STEP);
        } else {
          if (!UtilReRoll.askForReRollIfAvailable(getGameState(), actingPlayer.getPlayer(), ReRolledAction.CHAINSAW, minimumRoll, false)) {
            dropChainsawPlayer = true;
          }
        }
      }
      if (dropChainsawPlayer) {
        FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
        InjuryResult injuryResultAttacker = UtilInjury.handleInjury(this, InjuryType.CHAINSAW, null, actingPlayer.getPlayer(), attackerCoordinate, null, ApothecaryMode.ATTACKER); 
        if (injuryResultAttacker.isArmorBroken()) {
          publishParameters(UtilInjury.dropPlayer(this, actingPlayer.getPlayer()));
          publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
        }
        publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultAttacker));
        getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnFailure);
      }
    } else {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  // ByteArray serialization
  
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
  public StepFoulChainsaw initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_PUSHBACK.getFrom(jsonObject);
    return this;
  }
  
}
