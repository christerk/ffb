package com.balancedbytes.games.ffb.server.step.phase.special;

import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.UtilServerSteps;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Final step of the bomb sequence. Consumes all expected stepParameters.
 * 
 * Expects stepParameter CATCHER_ID to be set by a preceding step. Expects
 * stepParameter END_TURN to be set by a preceding step.
 *
 * @author Kalimar
 */
public final class StepEndBomb extends AbstractStep {

	private String fCatcherId;
	private boolean fEndTurn;
	private boolean fBombExploded;

	public StepEndBomb(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.END_BOMB;
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			switch (pParameter.getKey()) {
			case CATCHER_ID:
				fCatcherId = (String) pParameter.getValue();
				consume(pParameter);
				return true;
			case END_TURN:
				fEndTurn = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
				return true;
			case BOMB_EXPLODED:
				fBombExploded = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
				consume(pParameter);
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

	private void executeStep() {
		Game game = getGameState().getGame();
		game.setPassCoordinate(null);
		fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
		if (fEndTurn || (fCatcherId == null) || fBombExploded) {
			game.setHomePlaying(
					(TurnMode.BOMB_HOME == game.getTurnMode()) || (TurnMode.BOMB_HOME_BLITZ == game.getTurnMode()));
			if ((TurnMode.BOMB_HOME_BLITZ == game.getTurnMode()) || (TurnMode.BOMB_AWAY_BLITZ == game.getTurnMode())) {
				game.setTurnMode(TurnMode.BLITZ);
			} else {
				game.setTurnMode(TurnMode.REGULAR);
			}
			SequenceGenerator.getInstance().pushEndPlayerActionSequence(getGameState(), false, true, fEndTurn);
		} else {
			Player catcher = game.getPlayerById(fCatcherId);
			game.setHomePlaying(game.getTeamHome().hasPlayer(catcher));
			UtilServerSteps.changePlayerAction(this, fCatcherId, PlayerAction.THROW_BOMB, false);
			SequenceGenerator.getInstance().pushPassSequence(getGameState());
		}
		// stop immediate re-throwing of the bomb
		game.setPassCoordinate(null);
		game.setThrowerId(null);
		game.setThrowerAction(null);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
		return jsonObject;
	}

	@Override
	public StepEndBomb initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(jsonObject);
		return this;
	}

}
