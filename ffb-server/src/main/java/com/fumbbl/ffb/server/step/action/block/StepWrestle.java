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
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;

/**
 * Step in block sequence to handle skill WRESTLE.
 * <p>
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class StepWrestle extends AbstractStep {

	private final StepState state;

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case OLD_DEFENDER_STATE:
					state.oldDefenderState = (PlayerState) parameter.getValue();
					return true;
				default:
					break;
			}
		}
		return false;
	}

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
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.USING_WRESTLE_ATTACKER.addTo(jsonObject, state.usingWrestleAttacker);
		IServerJsonOption.USING_WRESTLE_DEFENDER.addTo(jsonObject, state.usingWrestleDefender);
		IServerJsonOption.PLAYER_STATE_OLD.addTo(jsonObject, state.oldDefenderState);
		return jsonObject;
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
	public StepWrestle initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.usingWrestleAttacker = IServerJsonOption.USING_WRESTLE_ATTACKER.getFrom(source, jsonObject);
		state.usingWrestleDefender = IServerJsonOption.USING_WRESTLE_DEFENDER.getFrom(source, jsonObject);
		state.oldDefenderState = IServerJsonOption.PLAYER_STATE_OLD.getFrom(source, jsonObject);
		return this;
	}

	public static class StepState {
		public ActionStatus status;
		public PlayerState oldDefenderState;

		public Boolean usingWrestleDefender;
		public Boolean usingWrestleAttacker;
	}

}
