package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameCacheMode;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestLoadTeam;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandScheduleGame;

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
      requestHomeTeam.setAdminGameIdListener(scheduleGameCommand.getAdminGameIdListener());
      getServer().getFumbblRequestProcessor().add(requestHomeTeam);
      FumbblRequestLoadTeam requestAwayTeam = new FumbblRequestLoadTeam(gameState, null, scheduleGameCommand.getTeamAwayId(), false, null);
      requestAwayTeam.setAdminGameIdListener(scheduleGameCommand.getAdminGameIdListener());
      getServer().getFumbblRequestProcessor().add(requestAwayTeam);
    } else {
      Team teamHome = gameCache.getTeamById(scheduleGameCommand.getTeamHomeId());
      gameCache.addTeamToGame(gameState, teamHome, true);
      Team teamAway = gameCache.getTeamById(scheduleGameCommand.getTeamAwayId());
      gameCache.addTeamToGame(gameState, teamAway, false);
      gameCache.queueDbUpdate(gameState);
      if (scheduleGameCommand.getAdminGameIdListener() != null) {
      	scheduleGameCommand.getAdminGameIdListener().setGameId(gameState.getId());
      }
    }
  }
  
}
