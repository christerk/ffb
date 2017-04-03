package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandCloseGame;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerCloseGame extends ServerCommandHandler {

  protected ServerCommandHandlerCloseGame(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_CLOSE_GAME;
  }

  public boolean handleCommand(ReceivedCommand pReceivedCommand) {
    InternalServerCommandCloseGame closeGameCommand = (InternalServerCommandCloseGame) pReceivedCommand.getCommand();
    getServer().getGameCache().closeGame(closeGameCommand.getGameId());
    return true;
  }
  
}
