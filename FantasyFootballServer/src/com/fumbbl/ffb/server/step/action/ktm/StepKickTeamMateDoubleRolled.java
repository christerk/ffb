package com.fumbbl.ffb.server.step.action.ktm;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeKTMCrowd;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerInjury;

@RulesCollection(RulesCollection.Rules.COMMON)
public class StepKickTeamMateDoubleRolled extends AbstractStep {
	private String fKickedPlayerId;
	private PlayerState fKickedPlayerState;
	private FieldCoordinate fKickedPlayerCoordinate;

	public StepId getId() {
		return StepId.KICK_TM_DOUBLE_ROLLED;
	}

	public StepKickTeamMateDoubleRolled(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case KICKED_PLAYER_ID:
				fKickedPlayerId = (String) pParameter.getValue();
				return true;
			case KICKED_PLAYER_STATE:
				fKickedPlayerState = (PlayerState) pParameter.getValue();
				return true;
			case KICKED_PLAYER_COORDINATE:
				fKickedPlayerCoordinate = (FieldCoordinate) pParameter.getValue();
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
		Player<?> kickedPlayer = game.getPlayerById(fKickedPlayerId);
		if ((kickedPlayer != null) && (fKickedPlayerCoordinate != null) && (fKickedPlayerState != null)
				&& (fKickedPlayerState.getId() > 0)) {
			game.getFieldModel().setPlayerCoordinate(kickedPlayer, fKickedPlayerCoordinate);
			game.getFieldModel().setPlayerState(game.getDefender(), fKickedPlayerState);
			game.setDefenderId(null);
			InjuryResult injury = UtilServerInjury.handleInjury(this, new InjuryTypeKTMCrowd(), null, kickedPlayer,
					fKickedPlayerCoordinate, null, null, ApothecaryMode.THROWN_PLAYER);
			publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injury));

			if (fKickedPlayerCoordinate.equals(game.getFieldModel().getBallCoordinate())) {
				game.getFieldModel().setBallMoving(true);
				publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
				publishParameter(
						new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.SCATTER_BALL));
			}

		}
		publishParameter(new StepParameter(StepParameterKey.KICKED_PLAYER_COORDINATE, null)); // avoid reset in end step
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.KICKED_PLAYER_ID.addTo(jsonObject, fKickedPlayerId);
		IServerJsonOption.KICKED_PLAYER_STATE.addTo(jsonObject, fKickedPlayerState);
		IServerJsonOption.KICKED_PLAYER_COORDINATE.addTo(jsonObject, fKickedPlayerCoordinate);
		return jsonObject;
	}

	@Override
	public StepKickTeamMateDoubleRolled initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fKickedPlayerId = IServerJsonOption.KICKED_PLAYER_ID.getFrom(game, jsonObject);
		fKickedPlayerState = IServerJsonOption.KICKED_PLAYER_STATE.getFrom(game, jsonObject);
		fKickedPlayerCoordinate = IServerJsonOption.KICKED_PLAYER_COORDINATE.getFrom(game, jsonObject);
		return this;
	}

}
