package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPrayers extends AbstractStep {
	private int tvHome, tvAway;

	public StepPrayers(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.PRAYERS;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case TV_AWAY:
					tvAway = (int) parameter.getValue();
					consume(parameter);
					return true;
				case TV_HOME:
					tvHome = (int) parameter.getValue();
					consume(parameter);
					return true;
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public void start() {

	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.TEAM_VALUE.addTo(jsonObject, tvHome);
		IServerJsonOption.OPPONENT_TEAM_VALUE.addTo(jsonObject, tvAway);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		tvHome = IServerJsonOption.TEAM_VALUE.getFrom(source, jsonObject);
		tvAway = IServerJsonOption.OPPONENT_TEAM_VALUE.getFrom(source, jsonObject);
		return this;
	}
}
