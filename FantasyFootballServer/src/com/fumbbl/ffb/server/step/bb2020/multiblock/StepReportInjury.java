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
import com.fumbbl.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepReportInjury extends AbstractStep {

	private InjuryResult injuryResult;

	public StepReportInjury(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.REPORT_INJURY;
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
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);

		JsonObject injuryResultObject = IServerJsonOption.INJURY_RESULT.getFrom(source, UtilJson.toJsonObject(jsonValue));

		if (injuryResultObject != null) {
			injuryResult = new InjuryResult().initFrom(source, injuryResultObject);
		}

		return this;
	}
}
