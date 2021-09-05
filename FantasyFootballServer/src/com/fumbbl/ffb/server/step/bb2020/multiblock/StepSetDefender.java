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
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSetDefender extends AbstractStep {
	private String defenderId;
	private boolean ignoreNullValue;

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
			for (StepParameter parameter : parameterSet.values()) {
				switch (parameter.getKey()) {
					case BLOCK_DEFENDER_ID:
						defenderId = (String) parameter.getValue();
						break;
					case IGNORE_NULL_VALUE:
						ignoreNullValue = (boolean) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
	}

	@Override
	public boolean setParameter(StepParameter pParameter) {
		if (pParameter != null) {
			switch (pParameter.getKey()) {
				case BLOCK_DEFENDER_ID:
				case GAZE_VICTIM_ID:
					defenderId = (String) pParameter.getValue();
					break;
				default:
					break;
			}
		}
		return super.setParameter(pParameter);
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		if (StringTool.isProvided(defenderId) || !ignoreNullValue) {
			getGameState().getGame().setDefenderId(defenderId);
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		IJsonOption.IGNORE_NULL_VALUE.addTo(jsonObject, ignoreNullValue);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		defenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		ignoreNullValue = IJsonOption.IGNORE_NULL_VALUE.getFrom(source, jsonObject);
		return this;
	}
}
