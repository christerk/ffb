package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerStat extends TalkHandler {
	public TalkHandlerStat() {
		super("/stat", 2, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		if (commands.length <= 2) {
			return;
		}
		int stat;
		try {
			stat = Integer.parseInt(commands[2]);
		} catch (NumberFormatException nfe) {
			return;
		}

		for (Player<?> genericPlayer : findPlayersInCommand(team, commands)) {
			if ((genericPlayer instanceof RosterPlayer) && (stat >= 0)) {
				RosterPlayer player = (RosterPlayer) genericPlayer;
				if ("ma".equalsIgnoreCase(commands[1])) {
					player.setMovement(stat);
					reportStatChange(server, gameState, player, "MA", stat);
				}
				if ("st".equalsIgnoreCase(commands[1])) {
					player.setStrength(stat);
					reportStatChange(server, gameState, player, "ST", stat);
				}
				if ("ag".equalsIgnoreCase(commands[1])) {
					player.setAgility(stat);
					reportStatChange(server, gameState, player, "AG", stat);
				}
				if ("pa".equalsIgnoreCase(commands[1])) {
					player.setPassing(stat);
					reportStatChange(server, gameState, player, "PA", stat);
				}
				if ("av".equalsIgnoreCase(commands[1])) {
					player.setArmour(stat);
					reportStatChange(server, gameState, player, "AV", stat);
				}
			}
		}
	}

	private void reportStatChange(FantasyFootballServer server, GameState pGameState, RosterPlayer pPlayer, String pStat, int pValue) {
		if ((pGameState != null) && (pPlayer != null)) {
			Game game = pGameState.getGame();
			Team team = game.getTeamHome().hasPlayer(pPlayer) ? game.getTeamHome() : game.getTeamAway();
			server.getCommunication().sendAddPlayer(pGameState, team.getId(), pPlayer,
				game.getFieldModel().getPlayerState(pPlayer), game.getGameResult().getPlayerResult(pPlayer));
			String info = "Set " + pStat + " stat of player " + pPlayer.getName() + " to " +
				pValue + ".";
			server.getCommunication().sendPlayerTalk(pGameState, null, info);
		}
	}
}
