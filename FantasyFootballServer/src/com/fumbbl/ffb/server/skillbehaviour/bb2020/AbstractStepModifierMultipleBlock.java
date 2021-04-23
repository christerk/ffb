package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsParameter;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractStepModifierMultipleBlock<T extends IStep, V extends StepStateMultipleRolls> extends StepModifier<T, V> {

	@Override
	public StepCommandStatus handleCommandHook(T step, V state, ClientCommandUseSkill useSkillCommand) {
		return StepCommandStatus.EXECUTE_STEP;
	}

	@Override
	public boolean handleExecuteStepHook(T step, V state) {
		Game game = step.getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (canBeSkipped(actingPlayer.getPlayer())) {
			step.getResult().setNextAction(StepAction.NEXT_STEP);
			return false;
		}

		if (state.firstRun) {
			state.firstRun = false;
			state.initialCount = state.blockTargets.size();
			state.blockTargets = state.blockTargets.stream().map(game::getPlayerById)
				.filter(opponentPlayer -> requiresRoll(actingPlayer.getPlayer(), opponentPlayer))
				.map(Player::getId).collect(Collectors.toList());

			for (String targetId: new ArrayList<>(state.blockTargets)) {
				roll(step, actingPlayer, targetId, false, state.minimumRolls, state);
			}
			state.reRollAvailableAgainst.addAll(state.blockTargets);
			decideNextStep(game, step, state);

		} else {
			if (!StringTool.isProvided(state.reRollTarget) || state.reRollSource == null) {
				nextStep(step, state);
			} else {
				if (UtilServerReRoll.useReRoll(step, state.reRollSource, actingPlayer.getPlayer())) {
					roll(step, actingPlayer, state.reRollTarget,true, state.minimumRolls, state);
				}
				state.reRollAvailableAgainst.remove(state.reRollTarget);
				decideNextStep(game, step, state);
			}
		}
		return false;
	}

	protected abstract ReRolledAction reRolledAction();

	protected abstract boolean requiresRoll(Player<?> actingPlayer, Player<?> opponentPlayer);

	protected abstract boolean canBeSkipped(Player<?> actingPlayer);

	protected abstract int skillRoll(T step);

	protected abstract int minimumRoll(Game game, Player<?> actingPlayer, Player<?> opponentPlayer);

	protected abstract IReport report(Game game, String playerId, boolean mayBlock, int actualRoll, int minimumRoll, boolean reRolling, String currentTargetId);

	protected abstract void unhandledTargetsCallback(T step, V state);

	protected abstract void cleanUp(T step, V state);

	private void nextStep(T step, V state) {
		if (StringTool.isProvided(state.goToLabelOnFailure) && state.blockTargets.size() == state.initialCount) {
			step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
		} else {
			unhandledTargetsCallback(step, state);
			step.getResult().setNextAction(StepAction.NEXT_STEP);
		}
		cleanUp(step, state);
	}

	private void decideNextStep(Game game, T step, V state) {
		if (state.blockTargets.isEmpty()) {
			step.getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			state.teamReRollAvailable = UtilServerReRoll.isTeamReRollAvailable(step.getGameState(), game.getActingPlayer().getPlayer());
			state.proReRollAvailable = UtilServerReRoll.isProReRollAvailable(game.getActingPlayer().getPlayer(), game);
			if (state.reRollAvailableAgainst.isEmpty() || (!state.teamReRollAvailable && !state.proReRollAvailable)) {
				nextStep(step, state);
			} else {
				state.reRollTarget = null;
				state.reRollSource = null;
				UtilServerDialog.showDialog(step.getGameState(), createDialogParameter(game.getActingPlayer().getPlayer(), state), false);
			}
		}
	}

	private void roll(T step, ActingPlayer actingPlayer, String currentTargetId, boolean reRolling, Map<String, Integer> minimumRolls, V state) {
		Game game = step.getGameState().getGame();
		Player<?> defender = game.getPlayerById(currentTargetId);
		int actualRoll = skillRoll(step);
		int minimumRoll = minimumRoll(game, actingPlayer.getPlayer(), defender);
		boolean mayBlock = DiceInterpreter.getInstance().isSkillRollSuccessful(actualRoll, minimumRoll);
		minimumRolls.put(currentTargetId, minimumRoll);
		step.getResult().addReport(report(step.getGameState().getGame(), actingPlayer.getPlayerId(), mayBlock, actualRoll, minimumRoll, reRolling, currentTargetId));
		if (mayBlock) {
			state.blockTargets.remove(currentTargetId);
			successFulRollCallback(step, currentTargetId);
		} else if (!reRolling) {
			failedRollEffect(step);
		}
	}

	protected abstract void successFulRollCallback(T step, String successfulId);

	protected abstract void failedRollEffect(T step);

	private DialogReRollForTargetsParameter createDialogParameter(Player<?> player, V state) {
		return new DialogReRollForTargetsParameter(player.getId(), state.blockTargets, reRolledAction(),
			state.minimumRolls, state.reRollAvailableAgainst, state.proReRollAvailable, state.teamReRollAvailable);
	}
}
