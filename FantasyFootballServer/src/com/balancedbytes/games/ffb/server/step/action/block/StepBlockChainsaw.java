package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
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
 * Step in block sequence to handle skill CHAINSAW.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_SUCCESS.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter END_TURN for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepBlockChainsaw extends AbstractStepWithReRoll {
	
	private String fGotoLabelOnSuccess;
	private String fGotoLabelOnFailure;
	
	public StepBlockChainsaw(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.BLOCK_CHAINSAW;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  				case GOTO_LABEL_ON_FAILURE:
  					fGotoLabelOnFailure = (String) parameter.getValue();
  					break;
  				case GOTO_LABEL_ON_SUCCESS:
  					fGotoLabelOnSuccess = (String) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (!StringTool.isProvided(fGotoLabelOnFailure)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
  	}
  	if (!StringTool.isProvided(fGotoLabelOnSuccess)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SUCCESS + " is not initialized.");
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
        	FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
        	InjuryResult injuryResultDefender = UtilInjury.handleInjury(this, InjuryType.CHAINSAW, actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER); 
          if (injuryResultDefender.isArmorBroken()) {
            publishParameters(UtilInjury.dropPlayer(this, game.getDefender()));
          }
          publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultDefender));
          getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnSuccess);
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
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addString(fGotoLabelOnFailure);
  	pByteList.addString(fGotoLabelOnSuccess);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnFailure = pByteArray.getString();
  	fGotoLabelOnSuccess = pByteArray.getString();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = toJsonValueTemp();
    IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, fGotoLabelOnSuccess);
    IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, fGotoLabelOnFailure);
    return jsonObject;
  }
  
  public StepBlockChainsaw initFrom(JsonValue pJsonValue) {
    initFromTemp(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(jsonObject);
    fGotoLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(jsonObject);
    return this;
  }

}
