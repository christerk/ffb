package com.fumbbl.ffb.server.step.bb2016.ttm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;

/**
 * Step in ttm sequence to fumble a ttm pass.
 *
 * Expects stepParameter THROWN_PLAYER_COORDINATE to be set by a preceding step.
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step. Expects
 * stepParameter THROWN_PLAYER_STATE to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public final class StepFumbleTtmPass extends AbstractStep {

	private FieldCoordinate fThrownPlayerCoordinate;
	private String fThrownPlayerId;
	private PlayerState fThrownPlayerState;

	public StepFumbleTtmPass(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.FUMBLE_TTM_PASS;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case THROWN_PLAYER_COORDINATE:
				fThrownPlayerCoordinate = (FieldCoordinate) parameter.getValue();
				break;
			case THROWN_PLAYER_ID:
				fThrownPlayerId = (String) parameter.getValue();
				return true;
			case THROWN_PLAYER_STATE:
				fThrownPlayerState = (PlayerState) parameter.getValue();
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
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		Game game = getGameState().getGame();
		Player<?> thrownPlayer = game.getPlayerById(fThrownPlayerId);
		if ((thrownPlayer != null) && (fThrownPlayerCoordinate != null) && (fThrownPlayerState != null)
				&& (fThrownPlayerState.getId() > 0)) {
			game.getFieldModel().setPlayerCoordinate(thrownPlayer, fThrownPlayerCoordinate);
			game.getFieldModel().setPlayerState(game.getDefender(), fThrownPlayerState);
			game.setDefenderId(null);
		}
		publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.THROWN_PLAYER_COORDINATE.addTo(jsonObject, fThrownPlayerCoordinate);
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		IServerJsonOption.THROWN_PLAYER_STATE.addTo(jsonObject, fThrownPlayerState);
		return jsonObject;
	}

	@Override
	public StepFumbleTtmPass initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fThrownPlayerCoordinate = IServerJsonOption.THROWN_PLAYER_COORDINATE.getFrom(source, jsonObject);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
		fThrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(source, jsonObject);
		return this;
	}

}
