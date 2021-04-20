package com.fumbbl.ffb.server.step.action.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;

/**
 * Step in the pass sequence to handle skill BOMBARDIER.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public final class StepBombardier extends AbstractStep {

	public class StepState {

	}

	private StepState state;

	public StepBombardier(GameState pGameState) {
		super(pGameState);

		state = new StepState();
	}

	public StepId getId() {
		return StepId.BOMBARDIER;
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

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// ByteArray serialization

	public int getByteArraySerializationVersion() {
		return 1;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		return super.toJsonValue();
	}

	@Override
	public StepBombardier initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		return this;
	}

}
