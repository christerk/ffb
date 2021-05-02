package com.fumbbl.ffb.server.step.action.common;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.UtilBox;

/**
 * Step in move sequence to drop the acting player.
 * <p>
 * Expects stepParameter INJURY_TYPE to be set by a preceding step.
 * <p>
 * Sets stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepFallDown extends AbstractStep {

	private InjuryTypeServer<?> fInjuryType;
	private FieldCoordinate fCoordinateFrom;

	public StepFallDown(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.FALL_DOWN;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case INJURY_TYPE:
					fInjuryType = (InjuryTypeServer<?>) parameter.getValue();
					return true;
				case COORDINATE_FROM:
					fCoordinateFrom = (FieldCoordinate) parameter.getValue();
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
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		InjuryResult injuryResultAttacker = UtilServerInjury.handleInjury(this, fInjuryType, null, actingPlayer.getPlayer(),
			playerCoordinate, fCoordinateFrom, null, ApothecaryMode.ATTACKER);
		publishParameters(UtilServerInjury.dropPlayer(this, actingPlayer.getPlayer(), ApothecaryMode.ATTACKER));
		if (actingPlayer.isSufferingBloodLust()) {
			game.getFieldModel().clearMoveSquares();
			PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.RESERVE));
			UtilBox.putPlayerIntoBox(game, actingPlayer.getPlayer());
		}
		publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultAttacker));
		if (fInjuryType.fallingDownCausesTurnover() && (game.getTurnMode() != TurnMode.PASS_BLOCK)) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.INJURY_TYPE.addTo(jsonObject, fInjuryType);
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		return jsonObject;
	}

	@Override
	public StepFallDown initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(game, jsonObject);
		fInjuryType = (InjuryTypeServer<?>) IServerJsonOption.INJURY_TYPE.getFrom(game, jsonObject);
		return this;
	}

}