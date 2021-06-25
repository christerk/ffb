package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepStallingPlayer extends AbstractStep {
	private String playerId;

	public StepStallingPlayer(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.STALLING_PLAYER;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		for (StepParameter parameter : pParameterSet.values()) {
			if (parameter.getKey() == StepParameterKey.PLAYER_ID) {
				playerId = (String) parameter.getValue();
			}
		}
	}

	@Override
	public void start() {
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, UtilJson.toJsonObject(pJsonValue));
		return this;
	}
}
