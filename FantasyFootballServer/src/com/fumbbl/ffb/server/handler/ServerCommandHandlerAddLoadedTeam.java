package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandAddLoadedTeam;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandFumbblTeamLoaded;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

/**
 * @author Kalimar
 */
public class ServerCommandHandlerAddLoadedTeam extends ServerCommandHandler {

	protected ServerCommandHandlerAddLoadedTeam(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_ADD_LOADED_TEAM;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {

		InternalServerCommandAddLoadedTeam command = (InternalServerCommandAddLoadedTeam) pReceivedCommand
			.getCommand();
		try {
			GameState gameState = command.getGameState();

			if (gameState == null) {
				gameState = getServer().getGameCache().getGameStateById(command.getGameId());
			}

			if (gameState != null) {
				Game game = gameState.getGame();
				Team team = command.getTeam();

				game.teamsAreSkeletons();

				Boolean homeTeam = command.getHomeTeam();

				if (homeTeam == null) {
					homeTeam = (!StringTool.isProvided(game.getTeamHome().getId())
						|| team.getId().equals(game.getTeamHome().getId()));
				}

				getServer().getGameCache().addTeamToGame(gameState, team, homeTeam);
				if (GameStatus.SCHEDULED == gameState.getStatus()) {
					if (StringTool.isProvided(game.getTeamHome().getId()) && StringTool.isProvided(game.getTeamAway().getId())) {
						// log game scheduled -->
						if (getServer().getDebugLog().isLogging(IServerLogLevel.WARN)) {
							String logEntry = "GAME SCHEDULED " + StringTool.print(game.getTeamHome().getName()) + " vs. " +
								StringTool.print(game.getTeamAway().getName());
							getServer().getDebugLog().log(IServerLogLevel.WARN, gameState.getId(), logEntry);
						}
						// <-- log game scheduled
					}
				} else {
					String coach = command.getCoach();
					Session session = pReceivedCommand.getSession();

					InternalServerCommandFumbblTeamLoaded loadedCommand = new InternalServerCommandFumbblTeamLoaded(
						gameState.getId(), coach, homeTeam, command.getAccountProperties());
					getServer().getCommunication().handleCommand(new ReceivedCommand(loadedCommand, session));
				}
			} else {
				getServer().getDebugLog().log(
					IServerLogLevel.ERROR,
					command.getGameId(),
					"No gamestate found in command or cache, should only happen if command was created during " +
						"scheduling a game and has been serialized"
				);
			}

		} catch (Exception e) {
			getServer().getDebugLog().log(command.getGameId(), e);
		}
		return true;
	}

}
