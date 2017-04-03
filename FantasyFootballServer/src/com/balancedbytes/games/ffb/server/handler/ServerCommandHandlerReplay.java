package com.balancedbytes.games.ffb.server.handler;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandReplay;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.request.ServerRequestLoadReplay;
import com.balancedbytes.games.ffb.server.util.UtilServerReplay;

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

  public boolean handleCommand(ReceivedCommand pReceivedCommand) {

    ClientCommandReplay replayCommand = (ClientCommandReplay) pReceivedCommand.getCommand();
    Session session = pReceivedCommand.getSession();
    int replayToCommandNr = replayCommand.getReplayToCommandNr();

    long gameId = replayCommand.getGameId();

    if (gameId == 0) {
      SessionManager sessionManager = getServer().getSessionManager();
      gameId = sessionManager.getGameIdForSession(pReceivedCommand.getSession());
    }
    
    if (gameId == 0) {
      return false;
    }
    
    // client signals that it has received the complete replay - socket can be closed
    /*
    if (replayToCommandNr < 0) {
      try {
        pReceivedCommand.getSession().close();
      } catch (IOException pIoException) {
        getServer().getDebugLog().log(gameId, pIoException);
      }
      return;
    }
    */
    
    GameState gameState = getServer().getGameCache().getGameStateById(gameId);
    if (gameState == null) {
      gameState = getServer().getGameCache().queryFromDb(gameId);
      getServer().getGameCache().addGame(gameState);
    }
    
    if (gameState != null) {
      UtilServerReplay.startServerReplay(gameState, replayToCommandNr, pReceivedCommand.getSession());

    } else {
      // game has been moved out of the db - request it from the backup service
      getServer().getRequestProcessor().add(new ServerRequestLoadReplay(replayCommand.getGameId(), replayToCommandNr, session, ServerRequestLoadReplay.LOAD_GAME));
    }
    
    return true;
    
  }
    
}
