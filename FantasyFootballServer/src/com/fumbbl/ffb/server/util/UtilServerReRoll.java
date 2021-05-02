package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogReRollParameter;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportReRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepResult;
import com.fumbbl.ffb.util.UtilCards;

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
		GameMechanic gameMechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		if (pReRollSource != null) {
			if (ReRollSources.TEAM_RE_ROLL == pReRollSource) {
				TurnData turnData = game.getTurnData();
				gameMechanic
					.updateTurnDataAfterReRollUsage(turnData);

				if (LeaderState.AVAILABLE.equals(turnData.getLeaderState())) {
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.LEADER, successful, 0));
					turnData.setLeaderState(LeaderState.USED);
				} else {
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.TEAM_RE_ROLL, successful, 0));
				}

				if (pPlayer.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)) {
					int roll = gameState.getDiceRoller().rollSkill();
					int minimumRoll = gameMechanic.minimumLonerRoll(pPlayer);
					successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
					stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.LONER, successful, roll));
				} else {
					successful = true;
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
						successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, gameMechanic.minimumProRoll());
						stepResult.addReport(new ReportReRoll(pPlayer.getId(), ReRollSources.PRO, successful, roll));
					}
				} else {
					successful = UtilCards.hasSkill(pPlayer, pReRollSource.getSkill(game));
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
			boolean proOption = isProReRollAvailable(player, game);
			reRollAvailable = (teamReRollOption || proOption);
			if (reRollAvailable) {
				Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
				String playerId = player.getId();
				UtilServerDialog.showDialog(gameState,
						new DialogReRollParameter(playerId, reRolledAction, minimumRoll, teamReRollOption, proOption, fumble),
						!actingTeam.hasPlayer(player));
			}
		}
		return reRollAvailable;
	}

	public static boolean isProReRollAvailable(Player<?> player, Game game) {
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		return (mechanic.eligibleForPro(game.getActingPlayer(), player) && player.hasSkillProperty(NamedProperties.canRerollOncePerTurn)
				&& !playerState.hasUsedPro());
	}

	public static boolean isTeamReRollAvailable(GameState pGameState, Player<?> pPlayer) {
		Game game = pGameState.getGame();
		Team actingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
		return (actingTeam.hasPlayer(pPlayer) && !game.getTurnData().isReRollUsed() && (game.getTurnData().getReRolls() > 0)
				&& game.getTurnMode() != TurnMode.KICKOFF && (game.getTurnMode() != TurnMode.PASS_BLOCK) && (game.getTurnMode() != TurnMode.DUMP_OFF)
				&& ((game.getTurnMode() != TurnMode.BOMB_HOME) || game.getTeamHome().hasPlayer(pPlayer))
				&& ((game.getTurnMode() != TurnMode.BOMB_HOME_BLITZ) || game.getTeamHome().hasPlayer(pPlayer))
				&& ((game.getTurnMode() != TurnMode.BOMB_AWAY) || game.getTeamAway().hasPlayer(pPlayer))
				&& ((game.getTurnMode() != TurnMode.BOMB_AWAY_BLITZ) || game.getTeamAway().hasPlayer(pPlayer)));
	}

}