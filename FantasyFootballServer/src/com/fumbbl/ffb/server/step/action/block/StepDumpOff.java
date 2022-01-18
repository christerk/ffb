package com.fumbbl.ffb.server.step.action.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
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
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;

/**
 * Step in block sequence to handle skill DUMP_OFF.
 * <p>
 * Expects stepParameter DEFENDER_POSITION to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepDumpOff extends AbstractStep {

	public static class StepState {
		public ActionStatus status;
		public Boolean usingDumpOff;
		public FieldCoordinate defenderPosition;
		public TurnMode oldTurnMode;
	}

	private final StepState state;

	public StepDumpOff(GameState pGameState) {
		super(pGameState);
		state = new StepState();
	}

	public StepId getId() {
		return StepId.DUMP_OFF;
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
			if (parameter.getKey() == StepParameterKey.DEFENDER_POSITION) {
				state.defenderPosition = (FieldCoordinate) parameter.getValue();
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
		IServerJsonOption.USING_DUMP_OFF.addTo(jsonObject, state.usingDumpOff);
		IServerJsonOption.DEFENDER_POSITION.addTo(jsonObject, state.defenderPosition);
		IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, state.oldTurnMode);
		return jsonObject;
	}

	@Override
	public StepDumpOff initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.usingDumpOff = IServerJsonOption.USING_DUMP_OFF.getFrom(game, jsonObject);
		state.defenderPosition = IServerJsonOption.DEFENDER_POSITION.getFrom(game, jsonObject);
		state.oldTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(game, jsonObject);
		return this;
	}

}
