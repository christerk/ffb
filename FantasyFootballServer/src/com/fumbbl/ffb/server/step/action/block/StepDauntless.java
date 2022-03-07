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
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.util.StringTool;

/**
 * Step in block sequence to handle skill DAUNTLESS.
 * <p>
 * Expects stepParameter USING_STAB to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepDauntless extends AbstractStepWithReRoll {

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case USING_STAB: {
					state.usingStab = (Boolean) parameter.getValue();
					return true;
				}
				case USING_VOMIT: {
					state.usingVomit = (Boolean) parameter.getValue();
					return true;
				}
				case USING_CHAINSAW: {
					state.usingChainsaw = (Boolean) parameter.getValue();
					return true;
				}
				default:
					break;
			}
		}
		return false;
	}

	private final StepState state;

	public StepDauntless(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.DAUNTLESS;
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
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.USING_STAB.addTo(jsonObject, state.usingStab);
		if (state.status != null) {
			IServerJsonOption.STATUS.addTo(jsonObject, state.status.name());
		}
		IServerJsonOption.USING_CHAINSAW.addTo(jsonObject, state.usingChainsaw);
		IServerJsonOption.USING_VOMIT.addTo(jsonObject, state.usingChainsaw);
		return jsonObject;
	}

	private void executeStep() {
		getGameState().executeStepHooks(this, state);
	}

	// JSON serialization

	@Override
	public StepDauntless initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.usingStab = IServerJsonOption.USING_STAB.getFrom(game, jsonObject);
		String statusString = IServerJsonOption.STATUS.getFrom(game, jsonObject);
		if (StringTool.isProvided(statusString)) {
			state.status = ActionStatus.valueOf(statusString);
		}
		state.usingChainsaw = toPrimitive(IServerJsonOption.USING_CHAINSAW.getFrom(game, jsonObject));
		state.usingVomit = toPrimitive(IServerJsonOption.USING_VOMIT.getFrom(game, jsonObject));
		return this;
	}

	public static class StepState {
		public ActionStatus status;
		public Boolean usingStab;
		public Boolean usingChainsaw;
		public Boolean usingVomit;

		public boolean usesSpecialAction() {
			return (usingChainsaw != null && usingChainsaw) || (usingVomit != null && usingVomit) || (usingStab != null && usingStab);
		}
	}

}
