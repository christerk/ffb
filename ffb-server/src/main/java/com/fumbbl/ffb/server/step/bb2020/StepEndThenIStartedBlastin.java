package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepEndThenIStartedBlastin extends AbstractStep {

	public StepEndThenIStartedBlastin(GameState pGameState) {
		super(pGameState);
	}

	private boolean endPlayerAction;

	@Override
	public StepId getId() {
		return StepId.END_THEN_I_STARTED_BLASTIN;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			if (parameter.getKey() == StepParameterKey.END_PLAYER_ACTION) {
				endPlayerAction = toPrimitive((Boolean) parameter.getValue());
				return true;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		getResult().setNextAction(StepAction.NEXT_STEP);
		Game game = getGameState().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(game.getActingPlayer().getPlayer());
		if (endPlayerAction || playerState.isProneOrStunned() || playerState.isCasualty() || playerState.getBase() == PlayerState.KNOCKED_OUT) {
			getGameState().getStepStack().clear();

			EndPlayerAction endPlayerActionGenerator = (EndPlayerAction) game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR).forName(SequenceGenerator.Type.EndPlayerAction.name());
			EndPlayerAction.SequenceParams params = new EndPlayerAction.SequenceParams(getGameState(), false, endPlayerAction, false);

			endPlayerActionGenerator.pushSequence(params);
		} else {
			UtilServerPlayerMove.updateMoveSquares(getGameState(), game.getActingPlayer().isJumping());
			ServerUtilBlock.updateDiceDecorations(game);
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		return this;
	}
}
