package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.dialog.DialogReRollForTargetsParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.BlitzState;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportFoulAppearanceRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.block.StepFoulAppearance;
import com.balancedbytes.games.ffb.server.step.action.block.StepFoulAppearance.StepState;
import com.balancedbytes.games.ffb.server.step.bb2020.multiblock.StepFoulAppearanceMultiple;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.skill.FoulAppearance;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RulesCollection(Rules.BB2020)
public class FoulAppearanceBehaviour extends SkillBehaviour<FoulAppearance> {
	public FoulAppearanceBehaviour() {
		super();

		registerModifier(new StepModifier<StepFoulAppearance, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepFoulAppearance step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepFoulAppearance step, StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				Player<?> defender;
				if (game.getFieldModel().getBlitzState() != null) {
					defender = game.getPlayerById(game.getFieldModel().getBlitzState().getSelectedPlayerId());
				} else {
					defender = game.getDefender();
				}

				if (UtilCards.hasSkill(defender, skill)
						&& !UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.forceRollBeforeBeingBlocked)) {
					boolean doRoll = true;
					if (ReRolledActions.FOUL_APPEARANCE == step.getReRolledAction()) {
						if ((step.getReRollSource() == null)
								|| !UtilServerReRoll.useReRoll(step, step.getReRollSource(), actingPlayer.getPlayer())) {
							doRoll = false;
							handleFailure(step, state, game, actingPlayer);
						}
					}
					if (doRoll) {
						int foulAppearanceRoll = step.getGameState().getDiceRoller().rollSkill();
						int minimumRoll = DiceInterpreter.getInstance().minimumRollResistingFoulAppearance();
						boolean mayBlock = DiceInterpreter.getInstance().isSkillRollSuccessful(foulAppearanceRoll, minimumRoll);
						boolean reRolled = ((step.getReRolledAction() == ReRolledActions.FOUL_APPEARANCE)
								&& (step.getReRollSource() != null));
						step.getResult().addReport(new ReportFoulAppearanceRoll(actingPlayer.getPlayerId(),
								mayBlock, foulAppearanceRoll, minimumRoll, reRolled, null));
						if (mayBlock) {
							step.getResult().setNextAction(StepAction.NEXT_STEP);
						} else {
							if (!UtilServerReRoll.askForReRollIfAvailable(step.getGameState(), actingPlayer.getPlayer(),
									ReRolledActions.FOUL_APPEARANCE, minimumRoll, false)) {
								handleFailure(step, state, game, actingPlayer);
							}
						}
						if (!mayBlock && !reRolled) {
							step.getResult().setSound(SoundId.EW);
						}
					}
				} else {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				}
				return false;
			}

			private void handleFailure(StepFoulAppearance step, StepState state, Game game, ActingPlayer actingPlayer) {
				actingPlayer.setHasBlocked(true);
				game.getTurnData().setTurnStarted(true);
				step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
				BlitzState blitzState = game.getFieldModel().getBlitzState();
				if (blitzState != null) {
					blitzState.failed();
					game.getTurnData().setBlitzUsed(true);
				}
			}
		});

		registerModifier(new StepModifier<StepFoulAppearanceMultiple, StepFoulAppearanceMultiple.StepState>() {
			@Override
			public StepCommandStatus handleCommandHook(StepFoulAppearanceMultiple step, StepFoulAppearanceMultiple.StepState state, ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepFoulAppearanceMultiple step, StepFoulAppearanceMultiple.StepState state) {
				Game game = step.getGameState().getGame();
				ActingPlayer actingPlayer = game.getActingPlayer();
				actingPlayer.setHasBlocked(true);

				if (UtilCards.hasSkillToCancelProperty(actingPlayer.getPlayer(), NamedProperties.forceRollBeforeBeingBlocked)) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
					return false;
				}

				if (state.firstRun) {
					state.firstRun = false;

					state.blockTargets = state.blockTargets.stream().map(game::getPlayerById)
						.filter(player -> UtilCards.hasSkillWithProperty(player, NamedProperties.forceRollBeforeBeingBlocked))
						.map(Player::getId).collect(Collectors.toList());

					for (String targetId: new ArrayList<>(state.blockTargets)) {
						roll(step, actingPlayer, state.blockTargets, targetId, false, state.minimumRolls);
					}
					state.reRollAvailableAgainst.addAll(state.blockTargets);
					decideNextStep(game, step, state);

				} else {
					if (!StringTool.isProvided(state.reRollTarget) || state.reRollSource == null) {
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					} else {
						if (UtilServerReRoll.useReRoll(step, state.reRollSource, actingPlayer.getPlayer())) {
							roll(step, actingPlayer, state.blockTargets, state.reRollTarget, true, state.minimumRolls);
						}
						state.reRollAvailableAgainst.remove(state.reRollTarget);
						decideNextStep(game, step, state);
					}
				}
				return false;
			}

			private void decideNextStep(Game game, StepFoulAppearanceMultiple step, StepFoulAppearanceMultiple.StepState state) {
				if (state.blockTargets.isEmpty()) {
					step.getResult().setNextAction(StepAction.NEXT_STEP);
				} else {
					state.teamReRollAvailable = UtilServerReRoll.isTeamReRollAvailable(step.getGameState(), game.getActingPlayer().getPlayer());
					state.proReRollAvailable = UtilServerReRoll.isProReRollAvailable(game.getActingPlayer().getPlayer(), game);
					if (state.reRollAvailableAgainst.isEmpty() || (!state.teamReRollAvailable && !state.proReRollAvailable)) {
						if (state.blockTargets.size() == 1) {
							step.publishParameter(new StepParameter(StepParameterKey.PLAYER_ID_TO_REMOVE, state.blockTargets.get(0)));
							step.getResult().setNextAction(StepAction.NEXT_STEP);
						} else {
							step.getResult().setNextAction(StepAction.GOTO_LABEL, state.goToLabelOnFailure);
						}
					} else {
						UtilServerDialog.showDialog(step.getGameState(), createDialogParameter(game.getActingPlayer().getPlayer(), state), false);
					}
				}
			}

			private void roll(StepFoulAppearanceMultiple step, ActingPlayer actingPlayer, List<String> targets, String currentTargetId, boolean reRolling, Map<String, Integer> minimumRolls) {
				int foulAppearanceRoll = step.getGameState().getDiceRoller().rollSkill();
				int minimumRoll = DiceInterpreter.getInstance().minimumRollResistingFoulAppearance();
				boolean mayBlock = DiceInterpreter.getInstance().isSkillRollSuccessful(foulAppearanceRoll, minimumRoll);
				minimumRolls.put(currentTargetId, minimumRoll);
				step.getResult().addReport(new ReportFoulAppearanceRoll(actingPlayer.getPlayerId(),
					mayBlock, foulAppearanceRoll, minimumRoll, reRolling, null, currentTargetId));
				if (mayBlock) {
					targets.remove(currentTargetId);
				} else if (!reRolling) {
					step.getResult().setSound(SoundId.EW);
				}
			}

			private DialogReRollForTargetsParameter createDialogParameter(Player<?> player, StepFoulAppearanceMultiple.StepState state) {
				return new DialogReRollForTargetsParameter(player.getId(), state.blockTargets,
					ReRolledActions.FOUL_APPEARANCE, state.minimumRolls,
					state.reRollAvailableAgainst, state.proReRollAvailable, state.teamReRollAvailable);
			}
		});
	}
}