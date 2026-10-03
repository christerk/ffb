package com.fumbbl.ffb.server.step.bb2025.action.select;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogReRollModifierChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandReRollModifierChoice;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.server.util.bb2025.JumpUpModifierSelectionService;
import com.fumbbl.ffb.util.StringTool;

import java.util.LinkedHashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepJumpUp extends AbstractStepWithReRoll {

	public static class StepState {
		public String goToLabelOnFailure;
		public int roll;
		public boolean awaitingRescue;
		public boolean reRollUsed;
		public boolean endPlayerAction;
		public final Set<Skill> selectedModifierSkills = new LinkedHashSet<>();
	}

	private final StepState state = new StepState();
	private final JumpUpModifierSelectionService selectionService = new JumpUpModifierSelectionService();

	public StepJumpUp(GameState gameState) {
		super(gameState);
	}

	@Override
	public StepId getId() {
		return StepId.JUMP_UP;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			for (StepParameter parameter : parameterSet.values()) {
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
	public StepCommandStatus handleCommand(ReceivedCommand receivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(receivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			if (!state.awaitingRescue || getReRolledAction() != ReRolledActions.JUMP_UP
				|| (state.reRollUsed && getReRollSource() != null)) {
				return StepCommandStatus.UNHANDLED_COMMAND;
			}
			state.awaitingRescue = false;
			if (getReRollSource() == null
				|| !UtilServerReRoll.useReRoll(this, getReRollSource(), getGameState().getGame().getActingPlayer().getPlayer())) {
				failJumpUp();
				return commandStatus;
			}
			state.reRollUsed = true;
			state.roll = 0;
			executeStep();
			return commandStatus;
		}
		if (commandStatus != StepCommandStatus.UNHANDLED_COMMAND) {
			return commandStatus;
		}
		Game game = getGameState().getGame();
		switch (receivedCommand.getId()) {
			case CLIENT_RE_ROLL_MODIFIER_CHOICE:
				ClientCommandReRollModifierChoice choice = (ClientCommandReRollModifierChoice) receivedCommand.getCommand();
				if (!isAwaitingChoice() || choice.getReRolledAction() != ReRolledActions.JUMP_UP
					|| !game.getActingPlayer().getPlayerId().equals(choice.getPlayerId())) {
					return StepCommandStatus.UNHANDLED_COMMAND;
				}
				Set<Skill> skills = new LinkedHashSet<>(choice.getSkills());
				if (skills.size() != choice.getSkills().size()
					|| selectionService.findOptions(game, state.roll).stream()
					.noneMatch(option -> new LinkedHashSet<>(option.getSkills()).equals(skills))) {
					return StepCommandStatus.UNHANDLED_COMMAND;
				}
				for (Skill skill : skills) {
					state.selectedModifierSkills.add(skill);
					game.getActingPlayer().markSkillUsed(skill);
					getResult().addReport(new ReportSkillUse(choice.getPlayerId(), skill, true, SkillUse.ADD_AGILITY_MODIFIER));
				}
				state.awaitingRescue = false;
				executeStep();
				return StepCommandStatus.EXECUTE_STEP;
			default:
				return commandStatus;
		}
	}

	private boolean isAwaitingChoice() {
		Game game = getGameState().getGame();
		if (!state.awaitingRescue || game.getActingPlayer().getPlayer() == null
			|| !(game.getDialogParameter() instanceof DialogReRollModifierChoiceParameter)) {
			return false;
		}
		DialogReRollModifierChoiceParameter dialog = (DialogReRollModifierChoiceParameter) game.getDialogParameter();
		return dialog.getReRolledAction() == ReRolledActions.JUMP_UP
			&& game.getActingPlayer().getPlayerId().equals(dialog.getPlayerId()) && state.roll == dialog.getRoll();
	}

	public void failJumpUp() {
		Game game = getGameState().getGame();
		Player<?> player = game.getActingPlayer().getPlayer();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		state.awaitingRescue = false;
		state.endPlayerAction = true;
		UtilServerDialog.hideDialog(getGameState());
		game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.PRONE).changeActive(false));
		publishParameter(new StepParameter(StepParameterKey.END_PLAYER_ACTION, true));
		getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
	}

	private void executeStep() {
		if (state.endPlayerAction) {
			failJumpUp();
			return;
		}
		// A source's player-choice dialog must survive a reconnect until its command completes the request.
		if (state.awaitingRescue && getGameState().getGame().getDialogParameter() != null
			&& !(getGameState().getGame().getDialogParameter() instanceof DialogReRollModifierChoiceParameter)) {
			getResult().setNextAction(StepAction.CONTINUE);
			return;
		}
		UtilServerDialog.hideDialog(getGameState());
		getGameState().executeStepHooks(this, state);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, state.goToLabelOnFailure);
		IServerJsonOption.JUMP_UP_ROLL.addTo(jsonObject, state.roll);
		IServerJsonOption.AWAITING_RESCUE.addTo(jsonObject, state.awaitingRescue);
		IServerJsonOption.RE_ROLL_USED.addTo(jsonObject, state.reRollUsed);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, state.endPlayerAction);
		JsonArray skills = new JsonArray();
		state.selectedModifierSkills.forEach(skill -> skills.add(skill.getName()));
		IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.addTo(jsonObject, skills);
		return jsonObject;
	}

	@Override
	public StepJumpUp initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		state.goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		state.roll = IServerJsonOption.JUMP_UP_ROLL.getFrom(source, jsonObject);
		state.awaitingRescue = IServerJsonOption.AWAITING_RESCUE.getFrom(source, jsonObject);
		state.reRollUsed = IServerJsonOption.RE_ROLL_USED.getFrom(source, jsonObject);
		state.endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		state.selectedModifierSkills.clear();
		JsonArray skills = IServerJsonOption.SELECTED_AGILITY_MODIFIER_SKILLS.getFrom(source, jsonObject);
		SkillFactory factory = source.getFactory(Factory.SKILL);
		for (JsonValue skill : skills) {
			state.selectedModifierSkills.add(factory.forName(skill.asString()));
		}
		return this;
	}
}
