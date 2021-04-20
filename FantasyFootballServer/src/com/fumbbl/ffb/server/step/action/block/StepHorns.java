package com.fumbbl.ffb.server.step.action.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepId;

/**
 * Step in block sequence to handle skill HORNS.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepHorns extends AbstractStep {

	public class StepState {
		public ActionStatus status;
		public Boolean usingHorns;
	}

	private StepState state;

	public StepHorns(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.HORNS;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		getGameState().executeStepHooks(this, state);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.USING_HORNS.addTo(jsonObject, state.usingHorns);
		return jsonObject;
	}

	@Override
	public StepHorns initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.usingHorns = IServerJsonOption.USING_HORNS.getFrom(game, jsonObject);
		return this;
	}

}
