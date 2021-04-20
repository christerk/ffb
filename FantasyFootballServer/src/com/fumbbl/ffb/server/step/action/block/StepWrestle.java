package com.fumbbl.ffb.server.step.action.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.ActionStatus;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;

/**
 * Step in block sequence to handle skill WRESTLE.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepWrestle extends AbstractStep {

	public static class StepState {
		public ActionStatus status;

		public Boolean usingWrestleDefender;
		public Boolean usingWrestleAttacker;
	}

	private StepState state;

	public StepWrestle(GameState pGameState) {
		super(pGameState, StepAction.NEXT_STEP);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.WRESTLE;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public void repeat() {
		super.repeat();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
				commandStatus = handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), state);
		}
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
		IServerJsonOption.USING_WRESTLE_ATTACKER.addTo(jsonObject, state.usingWrestleAttacker);
		IServerJsonOption.USING_WRESTLE_DEFENDER.addTo(jsonObject, state.usingWrestleDefender);
		return jsonObject;
	}

	@Override
	public StepWrestle initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.usingWrestleAttacker = IServerJsonOption.USING_WRESTLE_ATTACKER.getFrom(game, jsonObject);
		state.usingWrestleDefender = IServerJsonOption.USING_WRESTLE_DEFENDER.getFrom(game, jsonObject);
		return this;
	}

}
