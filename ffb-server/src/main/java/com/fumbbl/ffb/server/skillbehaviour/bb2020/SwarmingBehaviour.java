package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogSwarmingErrorParameter;
import com.fumbbl.ffb.dialog.DialogSwarmingPlayersParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.mixed.ReportSwarmingRoll;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.mixed.kickoff.StepSwarming;
import com.fumbbl.ffb.server.step.mixed.kickoff.StepSwarming.StepState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.skill.bb2020.Swarming;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(Rules.BB2020)
public class SwarmingBehaviour extends SkillBehaviour<Swarming> {
	public SwarmingBehaviour() {
		super();

		registerModifier(new StepModifier<StepSwarming, StepState>() {

			@Override
			public StepCommandStatus handleCommandHook(StepSwarming step, StepState state,
					ClientCommandUseSkill useSkillCommand) {
				return StepCommandStatus.EXECUTE_STEP;
			}

			@Override
			public boolean handleExecuteStepHook(StepSwarming step, StepState state) {
				Game game = step.getGameState().getGame();
				boolean hasSwarmingReserves = false;

				if (game.getTurnMode() == TurnMode.SWARMING) {
					if (state.endTurn) {
						state.endTurn = false;
						step.getResult().setSound(SoundId.DING);
						int placedSwarmingPlayers = 0;
						for (Player<?> player : game.getTeamById(state.teamId).getPlayers()) {
							PlayerState playerState = game.getFieldModel().getPlayerState(player);
							FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
							if (playerState.isActive() && !playerCoordinate.isBoxCoordinate()) {
								placedSwarmingPlayers++;
							}
						}

						if (placedSwarmingPlayers > state.allowedAmount) {
							UtilServerDialog.showDialog(step.getGameState(),
									new DialogSwarmingErrorParameter(state.allowedAmount, placedSwarmingPlayers), false);
						} else {

							leave(step, state, game, placedSwarmingPlayers);
						}
					}
				} else {
					if (!state.handleReceivingTeam) {
						step.getGameState().setKickingSwarmers(0);
					}
					state.teamId = swarmingTeam(state, game).getId();
					Set<Player<?>> playersOnPitch = new HashSet<>();
					Set<Player<?>> playersReserveNoSwarming = new HashSet<>();
					int swarmersOnPitch = 0;
					for (Player<?> player : game.getTeamById(state.teamId).getPlayers()) {
						FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
						if (FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)) {
							if (UtilCards.hasSkill(player, skill)) {
								swarmersOnPitch++;
							}
							playersOnPitch.add(player);
						} else if (game.getFieldModel().getPlayerState(player).getBase() == PlayerState.RESERVE) {
							if (UtilCards.hasSkill(player, skill)) {
								hasSwarmingReserves = true;
							} else {
								playersReserveNoSwarming.add(player);
							}
						}
					}
					state.limitingAmount = swarmersOnPitch;

					if (hasSwarmingReserves) {
						for (Player<?> player : playersOnPitch) {
							PlayerState playerState = game.getFieldModel().getPlayerState(player);
							game.getFieldModel().setPlayerState(player, playerState.changeActive(false));
						}

						for (Player<?> player : playersReserveNoSwarming) {
							PlayerState playerState = game.getFieldModel().getPlayerState(player);
							game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.PRONE));
						}

						if (state.handleReceivingTeam) {
							game.setHomePlaying(!game.isHomePlaying());
						}

						game.setTurnMode(TurnMode.SWARMING);
						step.getGameState().pushCurrentStepOnStack();

						state.rolledAmount = step.getGameState().getDiceRoller().rollSwarmingPlayers();
						state.allowedAmount = Math.min(state.limitingAmount, state.rolledAmount);
						step.getResult().addReport(new ReportSwarmingRoll(state.teamId, state.allowedAmount, state.rolledAmount, state.limitingAmount));
						if (state.allowedAmount == 0) {
							leave(step, state, game, 0);

							step.getResult().setNextAction(StepAction.NEXT_STEP);
						} else {
							UtilServerDialog.showDialog(step.getGameState(), new DialogSwarmingPlayersParameter(state.allowedAmount),
								false);
						}
					} else {
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
				}
				return false;
			}

			private void leave(StepSwarming step, StepState state, Game game, int placedSwarmingPlayers) {
				for (Player<?> player : game.getTeamById(state.teamId).getPlayers()) {
					PlayerState playerState = game.getFieldModel().getPlayerState(player);
					if (playerState.getBase() == PlayerState.PRONE) {
						game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
					}
				}

				game.setTurnMode(TurnMode.KICKOFF);
				UtilPlayer.refreshPlayersForTurnStart(game);
				game.getFieldModel().clearTrackNumbers();
				if (state.handleReceivingTeam) {
					game.setHomePlaying(!game.isHomePlaying());
				} else {
					step.getGameState().setKickingSwarmers(placedSwarmingPlayers);
				}
				step.getGameState().getStepStack().pop();
				step.getResult().setNextAction(StepAction.NEXT_STEP);
			}
		});
	}

	private Team swarmingTeam(StepState state, Game game) {
		if (state.handleReceivingTeam) {
			return game.isHomePlaying() ? game.getTeamAway() : game.getTeamHome();
		}
		return game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
	}
}
