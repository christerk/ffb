package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameCacheMode;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandScheduleGame;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestLoadTeam;

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

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    InternalServerCommandScheduleGame scheduleGameCommand = (InternalServerCommandScheduleGame) pReceivedCommand.getCommand();
    GameCache gameCache = getServer().getGameCache();
    GameState gameState = gameCache.createGameState(GameCacheMode.SCHEDULE_GAME);
    if (ServerMode.FUMBBL == getServer().getMode()) {
      FumbblRequestLoadTeam requestHomeTeam = new FumbblRequestLoadTeam(gameState, null, scheduleGameCommand.getTeamHomeId(), true, null);
      requestHomeTeam.setGameIdListener(scheduleGameCommand.getGameIdListener());
      getServer().getRequestProcessor().add(requestHomeTeam);
      FumbblRequestLoadTeam requestAwayTeam = new FumbblRequestLoadTeam(gameState, null, scheduleGameCommand.getTeamAwayId(), false, null);
      requestAwayTeam.setGameIdListener(scheduleGameCommand.getGameIdListener());
      getServer().getRequestProcessor().add(requestAwayTeam);
    } else {
      Team teamHome = gameCache.getTeamById(scheduleGameCommand.getTeamHomeId());
      gameCache.addTeamToGame(gameState, teamHome, true);
      Team teamAway = gameCache.getTeamById(scheduleGameCommand.getTeamAwayId());
      gameCache.addTeamToGame(gameState, teamAway, false);
      if (scheduleGameCommand.getGameIdListener() != null) {
      	scheduleGameCommand.getGameIdListener().setGameId(gameState.getId());
      }
    }
  }
  
}
