package com.fumbbl.ffb.server.step.bb2020.multiblock;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSetDefender extends AbstractStep {
	private String defenderId;

	public StepSetDefender(GameState pGameState) {
		super(pGameState);
	}

	public StepSetDefender(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	@Override
	public StepId getId() {
		return StepId.SET_DEFENDER;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		super.init(parameterSet);
		if (parameterSet != null) {
			for (StepParameter parameter: parameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.BLOCK_DEFENDER_ID) {
					defenderId = (String) parameter.getValue();
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
		getGameState().getGame().setDefenderId(defenderId);
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		defenderId = IJsonOption.DEFENDER_ID.getFrom(source, UtilJson.toJsonObject(pJsonValue));
		return this;
	}
}
