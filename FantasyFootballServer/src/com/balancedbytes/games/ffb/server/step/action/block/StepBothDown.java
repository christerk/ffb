package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle both down block result.
 * 
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepBothDown extends AbstractStep {

	private PlayerState fOldDefenderState;

	public StepBothDown(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.BOTH_DOWN;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case OLD_DEFENDER_STATE:
				fOldDefenderState = (PlayerState) pParameter.getValue();
				return true;
			default:
				break;
			}
		}
		return false;
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
		PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
		if (!game.getDefender().hasSkillWithProperty(NamedProperties.preventFallOnBothDown)) {
			game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
		} else {
			game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
		}
		if (!actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.preventFallOnBothDown)) {
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), attackerState.changeBase(PlayerState.FALLING));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
		return jsonObject;
	}

	@Override
	public StepBothDown initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		return this;
	}

}
