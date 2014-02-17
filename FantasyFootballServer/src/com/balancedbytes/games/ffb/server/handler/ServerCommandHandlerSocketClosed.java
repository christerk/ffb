package com.balancedbytes.games.ffb.server.handler;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestRemoveGamestate;
import com.balancedbytes.games.ffb.server.util.UtilTimer;
import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerSocketClosed extends ServerCommandHandler {

  protected ServerCommandHandlerSocketClosed(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_SOCKET_CLOSED;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    
    SessionManager sessionManager = getServer().getSessionManager();
    String coach = sessionManager.getCoachForSession(pReceivedCommand.getSession());
    ClientMode mode = sessionManager.getModeForSession(pReceivedCommand.getSession());
    long gameId = sessionManager.getGameIdForSession(pReceivedCommand.getSession());
    sessionManager.removeSession(pReceivedCommand.getSession());

    Session[] sessions = sessionManager.getSessionsForGameId(gameId);

    GameCache gameCache = getServer().getGameCache();
    GameState gameState = gameCache.getGameStateById(gameId);
    if (gameState != null) {
      
      int spectators = 0;
      for (int i = 0; i < sessions.length; i++) {
        if (sessionManager.getModeForSession(sessions[i]) == ClientMode.SPECTATOR) {
          spectators++;
        }
      }
      
      // stop timer whenever a player drops out
    	if (ClientMode.PLAYER == mode) {
      	UtilTimer.syncTime(gameState);
        UtilTimer.stopTurnTimer(gameState);
    	}
      
      Session homeSession = sessionManager.getSessionOfHomeCoach(gameState);
      Session awaySession = sessionManager.getSessionOfAwayCoach(gameState);

      if ((homeSession == null) && (awaySession == null) && ((GameStatus.STARTING == gameState.getStatus()) || (GameStatus.ACTIVE == gameState.getStatus()))) {
        gameState.setStatus(GameStatus.PAUSED);
        gameCache.queueDbUpdate(gameState, true);
        removeFumbblGame(gameState);
        gameState.fetchChanges();  // remove all changes from queue
      }

    	if (ArrayTool.isProvided(sessions)) {
    		getServer().getCommunication().sendLeave(sessions, coach, mode, spectators);
    	} else {
        getServer().getGameCache().removeGameStateFromCache(gameState);
    	}

    }
    
  }
  
  private void removeFumbblGame(GameState pGameState) {
    if (!pGameState.getGame().isTesting() && (getServer().getMode() == ServerMode.FUMBBL)) {
      getServer().getRequestProcessor().add(new FumbblRequestRemoveGamestate(pGameState));
    }
  }

}
