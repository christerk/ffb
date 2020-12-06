package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to handle skill DODGE.
 *
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * @author Kalimar
 */
public class StepBlockDodge extends AbstractStep {

	public static class StepState {
		public Boolean usingDodge;
		public PlayerState oldDefenderState;
	}

	private StepState state;

	public StepBlockDodge(GameState pGameState) {
		super(pGameState);

		state = new StepState();
	}

	public StepId getId() {
		return StepId.BLOCK_DODGE;
	}

	@Override
	public void start() {
		super.start();
		state = new StepState();
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
			switch (pParameter.getKey()) {
			case OLD_DEFENDER_STATE:
				state.oldDefenderState = (PlayerState) pParameter.getValue();
				return true;
			default:
				break;
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
		IServerJsonOption.USING_DODGE.addTo(jsonObject, state.usingDodge);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, state.oldDefenderState);
		return jsonObject;
	}

	@Override
	public StepBlockDodge initFrom(JsonValue pJsonValue) {
		super.initFrom(pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		state.usingDodge = IServerJsonOption.USING_DODGE.getFrom(jsonObject);
		state.oldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(jsonObject);
		return this;
	}

}
