package com.fumbbl.ffb.server.step.action.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerState;
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
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.StringTool;

/**
 * Step in block sequence to handle skill JUGGERNAUT.
 * 
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_SUCCESS.
 * 
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * Sets stepParameter BLOCK_RESULT for all steps on the stack.
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepJuggernaut extends AbstractStep {

	public static class StepState {
		public ActionStatus status;
		public Boolean usingJuggernaut;
		public PlayerState oldDefenderState;
		public String goToLabelOnSuccess;
	}

	private final StepState state;

	public StepJuggernaut(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.JUGGERNAUT;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_SUCCESS) {
					state.goToLabelOnSuccess = (String) parameter.getValue();
				}
			}
		}
		if (!StringTool.isProvided(state.goToLabelOnSuccess)) {
			throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_SUCCESS + " is not initialized.");
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
			commandStatus = handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), state);
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.OLD_DEFENDER_STATE) {
				state.oldDefenderState = (PlayerState) parameter.getValue();
				return true;
			}
		}
		return false;
	}

	private void executeStep() {
		getGameState().executeStepHooks(this, state);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.USING_JUGGERNAUT.addTo(jsonObject, state.usingJuggernaut);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, state.oldDefenderState);
		IServerJsonOption.GOTO_LABEL_ON_SUCCESS.addTo(jsonObject, state.goToLabelOnSuccess);
		return jsonObject;
	}

	@Override
	public StepJuggernaut initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.usingJuggernaut = IServerJsonOption.USING_JUGGERNAUT.getFrom(source, jsonObject);
		state.oldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		state.goToLabelOnSuccess = IServerJsonOption.GOTO_LABEL_ON_SUCCESS.getFrom(source, jsonObject);
		return this;
	}

}
