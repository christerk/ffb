package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.LeaderState;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.dialog.DialogReRollParameter;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.report.ReportReRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepResult;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 *
 * @author Kalimar
 */
public class UtilServerReRoll {

	public static boolean useReRoll(IStep pStep, ReRollSource pReRollSource, Player<?> pPlayer) {
		if (pPlayer == null) {
			throw new IllegalArgumentException("Parameter player must not be null.");
		}
		boolean successful = false;
		GameState gameState = pStep.getGameState();
		Game game = gameState.getGame();
		StepResult stepResult = pStep.getResult();
		if (pReRollSource != null) {
			if (ReRollSources.TEAM_RE_ROLL == pReRollSource) {
				TurnData turnData = game.getTurnData();
				turnData.setReRollUsed(true);
				turnData.setReRolls(turnData.getReRolls() - 1);

				if (pPlayer.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
					int roll = gameState.getDiceRoller().rollSkill();
					successful = DiceInterpreter.getInstance().isLonerSuccessful(roll);
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.LONER, successful, roll));
					// TODO: add a message for Leader reroll being used with Loner?
				} else {
					successful = true;
					if (LeaderState.AVAILABLE.equals(turnData.getLeaderState())) {
						stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.LEADER, successful, 0));
						turnData.setLeaderState(LeaderState.USED);
					} else {
						stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.TEAM_RE_ROLL, successful, 0));
					}
				}
			}
			if (pReRollSource.getSkill(game) != null) {
				if (ReRollSources.PRO == pReRollSource) {
					PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
					successful = (pPlayer.hasSkillProperty(NamedProperties.canRerollOncePerTurn)
							&& !playerState.hasUsedPro());
					if (successful) {
						game.getFieldModel().setPlayerState(pPlayer, playerState.changeUsedPro(true));
						int roll = gameState.getDiceRoller().rollSkill();
						successful = DiceInterpreter.getInstance().isProSuccessful(roll);
						stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.PRO, successful, roll));
					}
				} else {
					successful = UtilCards.hasSkill(game, pPlayer, pReRollSource.getSkill(game));
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), pReRollSource, successful, 0));
				}
				ActingPlayer actingPlayer = game.getActingPlayer();
				if (actingPlayer.getPlayer() == pPlayer) {
					actingPlayer.markSkillUsed(pReRollSource.getSkill(game));
				}
			}
		}
		return successful;
	}

	public static boolean askForReRollIfAvailable(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
			int minimumRoll, boolean fumble) {
		boolean reRollAvailable = false;
		Game game = gameState.getGame();
		if (minimumRoll >= 0) {
			boolean teamReRollOption = isTeamReRollAvailable(gameState, player);
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			boolean proOption = (player.hasSkillProperty(NamedProperties.canRerollOncePerTurn)
					&& !playerState.hasUsedPro());
			reRollAvailable = (teamReRollOption || proOption);
			if (reRollAvailable) {
				Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
				String playerId = (player != null) ? player.getId() : null;
				UtilServerDialog.showDialog(gameState,
						new DialogReRollParameter(playerId, reRolledAction, minimumRoll, teamReRollOption, proOption, fumble),
						!actingTeam.hasPlayer(player));
			}
		}
		return reRollAvailable;
	}

	public static boolean isTeamReRollAvailable(GameState pGameState, Player<?> pPlayer) {
		Game game = pGameState.getGame();
		Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
		// TODO: no team re-rolls on dump-off
		return (actingTeam.hasPlayer(pPlayer) && !game.getTurnData().isReRollUsed() && (game.getTurnData().getReRolls() > 0)
				&& game.getTurnMode() != TurnMode.KICKOFF && (game.getTurnMode() != TurnMode.PASS_BLOCK)
				&& ((game.getTurnMode() != TurnMode.BOMB_HOME) || game.getTeamHome().hasPlayer(pPlayer))
				&& ((game.getTurnMode() != TurnMode.BOMB_HOME_BLITZ) || game.getTeamHome().hasPlayer(pPlayer))
				&& ((game.getTurnMode() != TurnMode.BOMB_AWAY) || game.getTeamAway().hasPlayer(pPlayer))
				&& ((game.getTurnMode() != TurnMode.BOMB_AWAY_BLITZ) || game.getTeamAway().hasPlayer(pPlayer)));
	}

}
