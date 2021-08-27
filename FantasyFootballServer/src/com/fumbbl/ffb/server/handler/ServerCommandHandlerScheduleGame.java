package com.fumbbl.ffb.server.handler;

import java.util.ArrayList;

import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameStartMode;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandScheduleGame;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestLoadTeam;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerScheduleGame extends ServerCommandHandler {

	protected ServerCommandHandlerScheduleGame(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_SCHEDULE_GAME;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {
		InternalServerCommandScheduleGame scheduleGameCommand = (InternalServerCommandScheduleGame) pReceivedCommand
				.getCommand();
		GameCache gameCache = getServer().getGameCache();
		GameState gameState = gameCache.createGameState(GameStartMode.SCHEDULE_GAME);
		if (ServerMode.FUMBBL == getServer().getMode()) {
			FumbblRequestLoadTeam requestHomeTeam = new FumbblRequestLoadTeam(gameState, null,
					scheduleGameCommand.getTeamHomeId(), true, null, new ArrayList<String>());
			getServer().getRequestProcessor().add(requestHomeTeam);
			FumbblRequestLoadTeam requestAwayTeam = new FumbblRequestLoadTeam(gameState, null,
					scheduleGameCommand.getTeamAwayId(), false, null, new ArrayList<String>());
			getServer().getRequestProcessor().add(requestAwayTeam);
		} else {
			Team teamHome = gameCache.getTeamById(scheduleGameCommand.getTeamHomeId(), gameState.getGame());
			gameCache.addTeamToGame(gameState, teamHome, true);
			Team teamAway = gameCache.getTeamById(scheduleGameCommand.getTeamAwayId(), gameState.getGame());
			gameCache.addTeamToGame(gameState, teamAway, false);
		}
		if (scheduleGameCommand.getGameIdListener() != null) {
			scheduleGameCommand.getGameIdListener().setGameId(gameState.getId());
		}
		return true;
	}

}
