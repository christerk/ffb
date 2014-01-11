package com.balancedbytes.games.ffb.server.handler;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandReplay;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameCacheMode;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestLoadGame;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.util.UtilReplay;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerReplay extends ServerCommandHandler {

  protected ServerCommandHandlerReplay(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_REPLAY;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {

    ClientCommandReplay replayCommand = (ClientCommandReplay) pReceivedCommand.getCommand();
    int replayToCommandNr = replayCommand.getReplayToCommandNr();

    GameState gameState = null;
    if (replayCommand.getGameId() > 0) {
      gameState = getServer().getGameCache().getGameStateById(replayCommand.getGameId());
    } else {
      SessionManager sessionManager = getServer().getSessionManager();
      long gameId = sessionManager.getGameIdForSession(pReceivedCommand.getSession());
      gameState = getServer().getGameCache().getGameStateById(gameId);
    }

    // client signals that it has received the complete replay - socket can be closed
    if (replayToCommandNr < 0) {
    	try {
    	  pReceivedCommand.getSession().close();
    	} catch (IOException pIoException) {
    		getServer().getDebugLog().log((gameState != null) ? gameState.getId() : -1, pIoException);
    	}
    	return;
    }
    
    if (gameState == null) {
      gameState = loadGameStateById(pReceivedCommand.getSession(), replayCommand.getGameId());
    }
    
    if (gameState != null) {
    	UtilReplay.startServerReplay(gameState, replayToCommandNr, pReceivedCommand.getSession());
    }
    
  }
  
  // either returns immediately with the gameState
  // or queues a fumbbl request and returns with null
  private GameState loadGameStateById(Session pSession, long pGameId) {
    GameCache gameCache = getServer().getGameCache();
    GameState gameState = gameCache.getGameStateById(pGameId);
    if (gameState == null) {
      if (getServer().getMode() == ServerMode.FUMBBL) {
        getServer().getFumbblRequestProcessor().add(new FumbblRequestLoadGame(pGameId, null, null, ClientMode.REPLAY, pSession));
      } else {
      	gameState = gameCache.queryFromDb(pGameId);
        if (gameState != null) {
          Game game = gameState.getGame();
          game.getTeamHome().updateRoster(gameCache.getRosterById(game.getTeamHome().getRosterId()));
          game.getTeamAway().updateRoster(gameCache.getRosterById(game.getTeamAway().getRosterId()));
          gameCache.add(gameState, GameCacheMode.REPLAY_GAME);
        }
      }
    }
    return gameState;
  }
  
}
