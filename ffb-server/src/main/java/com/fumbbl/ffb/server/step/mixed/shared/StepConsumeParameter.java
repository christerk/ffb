package com.fumbbl.ffb.server.step.mixed.shared;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepConsumeParameter extends AbstractStep {

	private final Set<StepParameterKey> parameterToConsume = new HashSet<>();

	public StepConsumeParameter(GameState pGameState) {
		super(pGameState);
	}

	public StepConsumeParameter(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	@Override
	public StepId getId() {
		return StepId.CONSUME_PARAMETER;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			for (StepParameter parameter: parameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.CONSUME_PARAMETER) {
					parameterToConsume.addAll((Collection<? extends StepParameterKey>) parameter.getValue());
				}
			}
		}
		super.init(parameterSet);
	}

	@Override
	public void start() {
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null && parameterToConsume.contains(parameter.getKey())) {
			consume(parameter);
			return true;
		}

		return super.setParameter(parameter);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();

		String[] keys = parameterToConsume.stream().map(StepParameterKey::name).collect(Collectors.toList()).toArray(new String[] {});
		IJsonOption.STEP_PARAMETER_KEYS.addTo(jsonObject, keys);

		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		parameterToConsume.addAll(Arrays.stream(IJsonOption.STEP_PARAMETER_KEYS.getFrom(source, UtilJson.toJsonObject(jsonValue)))
			.map(StepParameterKey::valueOf).collect(Collectors.toSet()));
		return this;
	}
}
