package com.fumbbl.ffb.server.step.bb2020.multiblock;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepReportStabInjury extends AbstractStep {

	private String target;
	private InjuryResult injuryResult;

	public StepReportStabInjury(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.REPORT_STAB_INJURY;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			for (StepParameter parameter : parameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.PLAYER_ID) {
					target = (String) parameter.getValue();
				}
			}
		}
		if (!StringTool.isProvided(target)) {
			throw new StepException("StepParameter " + StepParameterKey.PLAYER_ID + " is not initialized.");
		}
	}

	@Override
	public void start() {
		super.start();
		if (injuryResult != null) {
			injuryResult.report(this);
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (injuryResult != null)  {
			IServerJsonOption.INJURY_RESULT.addTo(jsonObject, injuryResult.toJsonValue());
		}
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, target);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);

		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonObject injuryResultObject = IServerJsonOption.INJURY_RESULT.getFrom(source, jsonObject);

		if (injuryResultObject != null) {
			injuryResult = new InjuryResult().initFrom(source, injuryResultObject);
		}

		target = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}
}
