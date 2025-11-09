package com.fumbbl.ffb.server.devtools;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerMode;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class BallAndChainStandaloneSetup {

	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		properties.setProperty("server.test", "true");
		properties.setProperty("server.log.level", "5");

		FantasyFootballServer server = new FantasyFootballServer(ServerMode.STANDALONE, properties);

		GameCache cache = new GameCache(server);
		cache.init();

		GameState gameState = new GameState(server);
		Game game = new Game(server, server.getFactoryManager());
		game.initializeRules();

		gameState.setGame(game);

		loadTeam(cache, gameState, "teamBB25BallAndChainOffense", true);
		loadTeam(cache, gameState, "teamBB25BallAndChainDefense", false);

		printSummary(game, "Offense", game.getTeamHome());
		printSummary(game, "Defense", game.getTeamAway());

		System.out.println("Working directory: " + Paths.get("").toAbsolutePath());
		System.out.println("Loaded setups: BB25 Fanatic Test / BB25 Control Screen");
		System.out.println("Ready for manual hotseat testing.");
	}

	private static void loadTeam(GameCache cache, GameState gameState, String teamId, boolean home) {
		Game game = gameState.getGame();
		Team team = cache.getTeamById(teamId, game);
		if (home) {
			game.setTeamHome(team);
		} else {
			game.setTeamAway(team);
		}
	}

	private static void printSummary(Game game, String label, Team team) {
		System.out.println("=== " + label + " Team (" + team.getName() + ") ===");
		for (Player<?> player : team.getPlayers()) {
			StringBuilder sb = new StringBuilder();
			sb.append("#").append(player.getNr()).append(" ").append(player.getName());
			sb.append(" [").append(player.getPosition().getName()).append("]");
			Skill[] skills = player.getSkills();
			if (skills.length > 0) {
				sb.append(" skills=");
				String[] names = new String[skills.length];
				for (int i = 0; i < skills.length; i++) {
					names[i] = skills[i].getName();
				}
				sb.append(String.join(", ", names));
			}
			System.out.println(sb);
		}
		System.out.println();
	}
}

