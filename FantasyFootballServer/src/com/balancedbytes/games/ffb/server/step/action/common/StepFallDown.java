package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.InjuryType.InjuryTypeServer;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in move sequence to drop the acting player.
 * 
 * Expects stepParameter INJURY_TYPE to be set by a preceding step.
 * 
 * Sets stepParameter END_TURN for all steps on the stack. Sets stepParameter
 * INJURY_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepFallDown extends AbstractStep {

	private InjuryTypeServer fInjuryType;

	public StepFallDown(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.FALL_DOWN;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case INJURY_TYPE:
				fInjuryType = (InjuryTypeServer) pParameter.getValue();
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
				playerCoordinate, null, ApothecaryMode.ATTACKER);
		publishParameters(UtilServerInjury.dropPlayer(this, actingPlayer.getPlayer(), ApothecaryMode.ATTACKER));
		if (actingPlayer.isSufferingBloodLust()) {
			game.getFieldModel().clearMoveSquares();
			PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
			game.getFieldModel().setPlayerState(actingPlayer.getPlayer(), playerState.changeBase(PlayerState.RESERVE));
			UtilBox.putPlayerIntoBox(game, actingPlayer.getPlayer());
		}
		publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResultAttacker));
		if ((!fInjuryType.fallingDownCausesTurnover()) && (game.getTurnMode() != TurnMode.PASS_BLOCK)) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.INJURY_TYPE.addTo(jsonObject, fInjuryType);
		return jsonObject;
	}

	@Override
	public StepFallDown initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fInjuryType = (InjuryTypeServer) IServerJsonOption.INJURY_TYPE.getFrom(jsonObject);
		return this;
	}

}
