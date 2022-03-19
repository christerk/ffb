package com.fumbbl.ffb.server.step.bb2016;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;

/**
 * Step in block sequence to handle skill WILD ANIMAL.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * 
 * Sets stepParameter END_PLAYER_ACTION for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2016)
public class StepWildAnimal extends AbstractStepWithReRoll {

	public static class StepState {
		public ActionStatus status;
		public String goToLabelOnFailure;
	}

	private final StepState state;

	public StepWildAnimal(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.WILD_ANIMAL;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				switch (parameter.getKey()) {
				// mandatory
				case GOTO_LABEL_ON_FAILURE:
					state.goToLabelOnFailure = (String) parameter.getValue();
					break;
				default:
					break;
				}
			}
		}
		if (state.goToLabelOnFailure == null) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_FAILURE + " is not initialized.");
		}
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		getGameState().executeStepHooks(this, state);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, state.goToLabelOnFailure);
		return jsonObject;
	}

	@Override
	public StepWildAnimal initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		return this;
	}

}
