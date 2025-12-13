package com.fumbbl.ffb.server.step.mixed;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
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

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepEndThrowKeg extends AbstractStep {

	public StepEndThrowKeg(GameState pGameState) {
		super(pGameState);
	}

	private boolean endTurn;

	@Override
	public StepId getId() {
		return StepId.END_THROW_KEG;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			if (parameter.getKey() == StepParameterKey.END_TURN) {
				endTurn = toPrimitive((Boolean) parameter.getValue());
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

		EndPlayerAction endPlayerActionGenerator = (EndPlayerAction) game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR).forName(SequenceGenerator.Type.EndPlayerAction.name());
		EndPlayerAction.SequenceParams params = new EndPlayerAction.SequenceParams(getGameState(), false, true, endTurn);

		endPlayerActionGenerator.pushSequence(params);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		return this;
	}
}
