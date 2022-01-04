package com.fumbbl.ffb.server.step.bb2020.special;

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
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Move;
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
@RulesCollection(RulesCollection.Rules.BB2020)
public final class StepEndBomb extends AbstractStep {

	private String fCatcherId;
	private boolean fEndTurn;
	private boolean fBombExploded;
	private boolean allowMoveAfterPass;

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
	public void init(StepParameterSet parameterSet) {
		super.init(parameterSet);
		if (parameterSet != null) {
			for (StepParameter parameter: parameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.ALLOW_MOVE_AFTER_PASS) {
					allowMoveAfterPass = (boolean) parameter.getValue();
				}
			}
		}
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
			if (!fEndTurn && allowMoveAfterPass) {
				UtilServerSteps.changePlayerAction(this, game.getActingPlayer().getPlayerId(), PlayerAction.MOVE, false);
				((Move) factory.forName(SequenceGenerator.Type.Move.name()))
					.pushSequence(new Move.SequenceParams(getGameState()));
			} else {
				((EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name()))
					.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), false, true, fEndTurn));
			}
		} else {
			Player<?> catcher = game.getPlayerById(fCatcherId);
			game.setHomePlaying(game.getTeamHome().hasPlayer(catcher));
			UtilServerSteps.changePlayerAction(this, fCatcherId, PlayerAction.THROW_BOMB, false);
			((Pass)factory.forName(SequenceGenerator.Type.Pass.name()))
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
		IServerJsonOption.ALLOW_MOVE_AFTER_PASS.addTo(jsonObject, allowMoveAfterPass);
		return jsonObject;
	}

	@Override
	public StepEndBomb initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(game, jsonObject);
		fEndTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		allowMoveAfterPass = toPrimitive(IServerJsonOption.ALLOW_MOVE_AFTER_PASS.getFrom(game, jsonObject));
		return this;
	}

}
