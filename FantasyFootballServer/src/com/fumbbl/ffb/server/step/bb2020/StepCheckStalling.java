package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.report.bb2020.ReportStallerDetected;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepCheckStalling extends AbstractStep {
	private boolean ignoreActedFlag = true;

	public StepCheckStalling(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.CHECK_STALLING;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		for (StepParameter parameter : pParameterSet.values()) {
			if (parameter.getKey() == StepParameterKey.IGNORE_ACTED_FLAG) {
				ignoreActedFlag = (boolean) parameter.getValue();
			}
		}
	}

	@Override
	public void start() {
		if (performCheck()) {
			String stallingPlayer = findStallingPlayer();
			getResult().addReport(new ReportStallerDetected(stallingPlayer));
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private String findStallingPlayer() {
		return null;
	}

	private boolean performCheck() {
		return ((GameOptionBoolean) getGameState().getGame().getOptions().getOptionWithDefault(GameOptionId.ENABLE_STALLING_CHECK)).isEnabled()
			&& getGameState().getPrayerState().shouldNotStall(getGameState().getGame().getActingTeam())
			&& (ignoreActedFlag || getGameState().getGame().getActingPlayer().hasActed());
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.IGNORE_ACTED_FLAG.addTo(jsonObject, ignoreActedFlag);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		ignoreActedFlag = IServerJsonOption.IGNORE_ACTED_FLAG.getFrom(source, UtilJson.toJsonObject(pJsonValue));
		return this;
	}
}
