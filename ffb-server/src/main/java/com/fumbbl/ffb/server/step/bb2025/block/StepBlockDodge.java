package com.fumbbl.ffb.server.step.bb2025.block;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.action.block.UtilBlockSequence;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.bb2025.DodgeBlockChoiceService;

/**
 * Step in block sequence to handle skill DODGE.
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * Whether the defending coach is asked depends on their SETTING_DODGE_BLOCK.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepBlockDodge extends AbstractStep {

	public static class StepState {
		public Boolean usingDodge;
		public Boolean askForSkill;
		public PlayerState oldDefenderState;
	}

	private final StepState state;

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
		Game game = getGameState().getGame();
		if (state.askForSkill == null) {
			boolean defenderIsHome = game.getTeamHome().hasPlayer(game.getDefender());
			String setting = getGameState().getGameplaySetting(defenderIsHome, CommonProperty.SETTING_DODGE_BLOCK);
			state.askForSkill = new DodgeBlockChoiceService().askForDodge(game, setting);
		}
		UtilServerDialog.hideDialog(getGameState());
		boolean waitForDialog = getGameState().executeStepHooks(this, state);

		if (waitForDialog) {
			return;
		}

		if (toPrimitive(state.usingDodge)) {
			game.getFieldModel().setPlayerState(game.getDefender(), state.oldDefenderState);
		} else {
			PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
			game.getFieldModel().setPlayerState(game.getDefender(), defenderState.changeBase(PlayerState.FALLING));
		}

		publishParameters(UtilBlockSequence.initPushback(this));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.USING_DODGE.addTo(jsonObject, state.usingDodge);
		IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, state.oldDefenderState);
		IServerJsonOption.ASK_FOR_SKILL.addTo(jsonObject, state.askForSkill);
		return jsonObject;
	}

	@Override
	public StepBlockDodge initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.usingDodge = IServerJsonOption.USING_DODGE.getFrom(source, jsonObject);
		state.oldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(source, jsonObject);
		state.askForSkill = IServerJsonOption.ASK_FOR_SKILL.getFrom(source, jsonObject);
		return this;
	}

}
