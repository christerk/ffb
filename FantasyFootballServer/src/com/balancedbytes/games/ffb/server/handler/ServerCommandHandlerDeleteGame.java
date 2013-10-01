package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandDeleteGame;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerDeleteGame extends ServerCommandHandler {

  protected ServerCommandHandlerDeleteGame(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_DELETE_GAME;
  }

  public void handleNetCommand(NetCommand pNetCommand) {
    InternalServerCommandDeleteGame deleteGameCommand = (InternalServerCommandDeleteGame) pNetCommand;
    getServer().getGameCache().closeGame(deleteGameCommand.getGameId());
    getServer().getGameCache().queueDbDelete(deleteGameCommand.getGameId());
  }
  
}
