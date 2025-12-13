package com.fumbbl.ffb.server.step.mixed.pass;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
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
import com.fumbbl.ffb.util.StringTool;

/**
 * Step in the pass sequence to handle skill HAIL_MARY_PASS.
 * <p>
 * Needs to be initialized with stepParameter GOTO_LABEL_ON_FAILURE.
 * <p>
 * Sets stepParameter PASS_FUMBLE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepHailMaryPass extends AbstractStepWithReRoll {

	private final StepState state;

	public StepHailMaryPass(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.HAIL_MARY_PASS;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				// mandatory
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
					state.goToLabelOnFailure = (String) parameter.getValue();
				}
			}
		}
		if (!StringTool.isProvided(state.goToLabelOnFailure)) {
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
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND && pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
			ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
			if (commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canAddStrengthToPass)) {
				state.usingModifyingSkill = commandUseSkill.isSkillUsed();
				commandStatus = StepCommandStatus.EXECUTE_STEP;
			} else {
				commandStatus = handleSkillCommand((ClientCommandUseSkill) pReceivedCommand.getCommand(), state);
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void executeStep() {
		getGameState().executeStepHooks(this, state);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, state.goToLabelOnFailure);
		IServerJsonOption.PASS_RESULT.addTo(jsonObject, state.result);
		IServerJsonOption.PASS_SKILL_USED.addTo(jsonObject, state.passSkillUsed);
		IServerJsonOption.USING_MODIFYING_SKILL.addTo(jsonObject, state.usingModifyingSkill);
		IServerJsonOption.MINIMUM_ROLL.addTo(jsonObject, state.minimumRoll);
		IServerJsonOption.ROLL.addTo(jsonObject, state.roll);
		return jsonObject;
	}

	// JSON serialization

	@Override
	public StepHailMaryPass initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		state.result = (PassResult) IServerJsonOption.PASS_RESULT.getFrom(source, jsonObject);
		if (state.result == null) {
			Boolean fumble = IServerJsonOption.PASS_FUMBLE.getFrom(source, jsonObject);
			boolean passFumble = fumble != null && fumble;
			state.result = passFumble ? PassResult.FUMBLE : PassResult.INACCURATE;
		}
		state.passSkillUsed = IServerJsonOption.PASS_SKILL_USED.getFrom(source, jsonObject);
		state.usingModifyingSkill = IServerJsonOption.USING_MODIFYING_SKILL.getFrom(source, jsonObject);
		state.roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		state.minimumRoll = IServerJsonOption.MINIMUM_ROLL.getFrom(source, jsonObject);
		return this;
	}

	public static class StepState {
		public String goToLabelOnFailure;
		public PassResult result;
		public boolean passSkillUsed;
		public Boolean usingModifyingSkill;
		public int minimumRoll, roll;
	}

}
