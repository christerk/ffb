package com.fumbbl.ffb.server.step.bb2020.ttm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.ScatterPlayer;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

/**
 * Step to end ttm scatter sequence.
 * <p>
 * Expects stepParameter THROWN_PLAYER_ID to be set by a preceding step. Expects
 * stepParameter THROWN_PLAYER_STATE to be set by a preceding step. Expects
 * stepParameter THROWN_PLAYER_HAS_BALL to be set by a preceding step. Expects
 * stepParameter THROWN_PLAYER_COORDINATE to be set by a preceding step.
 * <p>
 * Consumes all known parameters. May push new scatterPlayerSequence on the
 * stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepEndScatterPlayer extends AbstractStep {

	private String fThrownPlayerId;
	private boolean fThrownPlayerHasBall;
	private PlayerState fThrownPlayerState;
	private FieldCoordinate fThrownPlayerCoordinate;
	private boolean fIsKickedPlayer, crashLanding;

	public StepEndScatterPlayer(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_SCATTER_PLAYER;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
				case KICKED_PLAYER_ID:
				case THROWN_PLAYER_ID:
					fThrownPlayerId = (String) pParameter.getValue();
					consume(pParameter);
					return true;
				case KICKED_PLAYER_HAS_BALL:
				case THROWN_PLAYER_HAS_BALL:
					fThrownPlayerHasBall = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					consume(pParameter);
					return true;
				case KICKED_PLAYER_STATE:
				case THROWN_PLAYER_STATE:
					fThrownPlayerState = (PlayerState) pParameter.getValue();
					consume(pParameter);
					return true;
				case KICKED_PLAYER_COORDINATE:
				case THROWN_PLAYER_COORDINATE:
					fThrownPlayerCoordinate = (FieldCoordinate) pParameter.getValue();
					return true;
				case IS_KICKED_PLAYER:
					fIsKickedPlayer = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
					return true;
				case CRASH_LANDING:
					crashLanding = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
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
		if ((thrownPlayer != null) && (fThrownPlayerState != null) && (fThrownPlayerCoordinate != null)) {
			SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
			((ScatterPlayer) factory.forName(SequenceGenerator.Type.ScatterPlayer.name()))
				.pushSequence(new ScatterPlayer.SequenceParams(getGameState(), fThrownPlayerId, fThrownPlayerState,
					fThrownPlayerHasBall, fThrownPlayerCoordinate, false, false, false, crashLanding, fIsKickedPlayer));
			if (fIsKickedPlayer) {
				publishParameter(new StepParameter(StepParameterKey.IS_KICKED_PLAYER, true));
			}
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		IServerJsonOption.THROWN_PLAYER_STATE.addTo(jsonObject, fThrownPlayerState);
		IServerJsonOption.THROWN_PLAYER_HAS_BALL.addTo(jsonObject, fThrownPlayerHasBall);
		IServerJsonOption.THROWN_PLAYER_COORDINATE.addTo(jsonObject, fThrownPlayerCoordinate);
		IServerJsonOption.IS_KICKED_PLAYER.addTo(jsonObject, fIsKickedPlayer);
		IServerJsonOption.CRASH_LANDING.addTo(jsonObject, crashLanding);
		return jsonObject;
	}

	@Override
	public StepEndScatterPlayer initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(game, jsonObject);
		fThrownPlayerState = IServerJsonOption.THROWN_PLAYER_STATE.getFrom(game, jsonObject);
		fThrownPlayerHasBall = IServerJsonOption.THROWN_PLAYER_HAS_BALL.getFrom(game, jsonObject);
		fThrownPlayerCoordinate = IServerJsonOption.THROWN_PLAYER_COORDINATE.getFrom(game, jsonObject);
		fIsKickedPlayer = IServerJsonOption.IS_KICKED_PLAYER.getFrom(game, jsonObject);
		crashLanding = IServerJsonOption.CRASH_LANDING.getFrom(game, jsonObject);
		return this;
	}

}
