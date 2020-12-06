package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.ActionStatus;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill DUMP_OFF.
 * 
 * Expects stepParameter DEFENDER_POSITION to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepDumpOff extends AbstractStep {

	public static class StepState {
		public ActionStatus status;
		public Boolean usingDumpOff;
		public FieldCoordinate defenderPosition;
		public TurnMode oldTurnMode;
	}

	private StepState state;

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
	public boolean setParameter(StepParameter pParameter) {
		if ((pParameter != null) && !super.setParameter(pParameter)) {
			if (pParameter.getKey() == StepParameterKey.DEFENDER_POSITION) {
				state.defenderPosition = (FieldCoordinate) pParameter.getValue();
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
	public StepDumpOff initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.usingDumpOff = IServerJsonOption.USING_DUMP_OFF.getFrom(jsonObject);
		state.defenderPosition = IServerJsonOption.DEFENDER_POSITION.getFrom(jsonObject);
		state.oldTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(jsonObject);
		return this;
	}

}
