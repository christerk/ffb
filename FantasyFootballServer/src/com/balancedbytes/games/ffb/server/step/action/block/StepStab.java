package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilInjury;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill STAB.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_SUCCESS.
 * 
 * Expects stepParameter USING_STAB to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepStab extends AbstractStep {
	
	private String fGotoLabelOnSuccess;
	private Boolean fUsingStab;

	public StepStab(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.STAB;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  				case GOTO_LABEL_ON_SUCCESS:
  					fGotoLabelOnSuccess = (String) parameter.getValue();
  					break;
					default:
						break;
  			}
  		}
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
  public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
    StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
    if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
      executeStep();
    }
    return commandStatus;
  }
	
	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case USING_STAB:
					fUsingStab = (Boolean) pParameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}
	
  private void executeStep() {
  	Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (UtilCards.hasSkill(game, actingPlayer, Skill.STAB) && (fUsingStab != null) && fUsingStab) {
      getResult().setSound(Sound.STAB);
      FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
      InjuryResult injuryResultDefender = UtilInjury.handleInjury(this, InjuryType.STAB, actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, ApothecaryMode.DEFENDER);
      if (injuryResultDefender.isArmorBroken()) {
        publishParameters(UtilInjury.dropPlayer(this, game.getDefender()));
      }
      publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultDefender));
      getResult().setNextAction(StepAction.GOTO_LABEL, fGotoLabelOnSuccess);
    } else {
    	getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }

  // ByteArray serialization
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fGotoLabelOnSuccess = pByteArray.getString();
  	fUsingStab = pByteArray.getBoolean();
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, fGotoLabelOnSuccess);
    IServerJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
    return jsonObject;
  }
  
  @Override
  public StepStab initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fGotoLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(jsonObject);
    fUsingStab = IServerJsonOption.USING_STAB.getFrom(jsonObject);
    return this;
  }

}
