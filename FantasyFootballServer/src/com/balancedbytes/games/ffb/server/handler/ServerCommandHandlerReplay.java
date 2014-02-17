package com.balancedbytes.games.ffb.server.handler;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandReplay;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.request.ServerRequestLoadReplay;
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
    Session session = pReceivedCommand.getSession();
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

    if (gameState != null) {
      UtilReplay.startServerReplay(gameState, replayToCommandNr, pReceivedCommand.getSession());

    } else {
      getServer().getRequestProcessor().add(new ServerRequestLoadReplay(replayCommand.getGameId(), replayToCommandNr, session));
    }
    
  }
    
}
