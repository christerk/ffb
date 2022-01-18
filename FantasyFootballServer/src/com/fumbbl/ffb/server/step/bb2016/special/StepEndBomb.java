package com.fumbbl.ffb.server.step.bb2016.special;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Pass;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

/**
 * Final step of the bomb sequence. Consumes all expected stepParameters.
 *
 * Expects stepParameter CATCHER_ID to be set by a preceding step. Expects
 * stepParameter END_TURN to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
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
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
			case CATCHER_ID:
				fCatcherId = (String) parameter.getValue();
				consume(parameter);
				return true;
			case END_TURN:
				fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
				return true;
			case BOMB_EXPLODED:
				fBombExploded = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
				consume(parameter);
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
		SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
		fEndTurn |= UtilServerSteps.checkTouchdown(getGameState());
		if (fEndTurn || (fCatcherId == null) || fBombExploded) {
			game.setHomePlaying(
					(TurnMode.BOMB_HOME == game.getTurnMode()) || (TurnMode.BOMB_HOME_BLITZ == game.getTurnMode()));
			if ((TurnMode.BOMB_HOME_BLITZ == game.getTurnMode()) || (TurnMode.BOMB_AWAY_BLITZ == game.getTurnMode())) {
				game.setTurnMode(TurnMode.BLITZ);
			} else {
				game.setTurnMode(TurnMode.REGULAR);
			}
			((EndPlayerAction)factory.forName(SequenceGenerator.Type.EndPlayerAction.name()))
				.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), false, true, fEndTurn));
		} else {
			Player<?> catcher = game.getPlayerById(fCatcherId);
			game.setHomePlaying(game.getTeamHome().hasPlayer(catcher));
			UtilServerSteps.changePlayerAction(this, fCatcherId, PlayerAction.THROW_BOMB, false);
			((com.fumbbl.ffb.server.step.generator.Pass)factory.forName(SequenceGenerator.Type.Pass.name()))
				.pushSequence(new Pass.SequenceParams(getGameState(), null));
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
	public StepEndBomb initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(game, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		return this;
	}

}
