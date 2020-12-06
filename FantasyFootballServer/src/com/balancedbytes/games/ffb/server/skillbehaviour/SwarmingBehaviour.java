package com.balancedbytes.games.ffb.server.skillbehaviour;

import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogSwarmingErrorParameter;
import com.balancedbytes.games.ffb.dialog.DialogSwarmingPlayersParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.report.ReportSwarmingRoll;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.model.StepModifier;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepSwarming;
import com.balancedbytes.games.ffb.server.step.phase.kickoff.StepSwarming.StepState;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.skill.Swarming;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

public class SwarmingBehaviour extends SkillBehaviour<Swarming> {
	public SwarmingBehaviour() {
		super();

		registerModifier(new StepModifier<StepSwarming, StepSwarming.StepState>() {

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
						for (Player player : game.getTeamById(state.teamId).getPlayers()) {
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

							for (Player player : game.getTeamById(state.teamId).getPlayers()) {
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
					}
				} else {
					if (!state.handleReceivingTeam) {
						step.getGameState().setKickingSwarmers(0);
					}
					state.teamId = swarmingTeam(state, game).getId();
					Set<Player> playersOnPitch = new HashSet<>();
					Set<Player> playersReserveNoSwarming = new HashSet<>();
					for (Player player : game.getTeamById(state.teamId).getPlayers()) {
						FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
						if (FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)) {
							playersOnPitch.add(player);
						} else if (game.getFieldModel().getPlayerState(player).getBase() == PlayerState.RESERVE) {
							if (UtilCards.hasSkill(game, player, skill)) {
								hasSwarmingReserves = true;
							} else {
								playersReserveNoSwarming.add(player);
							}
						}
					}

					if (hasSwarmingReserves) {
						for (Player player : playersOnPitch) {
							PlayerState playerState = game.getFieldModel().getPlayerState(player);
							game.getFieldModel().setPlayerState(player, playerState.changeActive(false));
						}

						for (Player player : playersReserveNoSwarming) {
							PlayerState playerState = game.getFieldModel().getPlayerState(player);
							game.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.PRONE));
						}

						if (state.handleReceivingTeam) {
							game.setHomePlaying(!game.isHomePlaying());
						}

						game.setTurnMode(TurnMode.SWARMING);
						step.getGameState().pushCurrentStepOnStack();

						state.allowedAmount = step.getGameState().getDiceRoller().rollSwarmingPlayers();
						step.getResult().addReport(new ReportSwarmingRoll(state.teamId, state.allowedAmount));
						UtilServerDialog.showDialog(step.getGameState(), new DialogSwarmingPlayersParameter(state.allowedAmount),
								false);
					} else {
						step.getResult().setNextAction(StepAction.NEXT_STEP);
					}
				}
				return false;
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