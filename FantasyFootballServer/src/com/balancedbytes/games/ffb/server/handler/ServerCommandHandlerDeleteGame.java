package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
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

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    InternalServerCommandDeleteGame deleteGameCommand = (InternalServerCommandDeleteGame) pReceivedCommand.getCommand();
    getServer().getGameCache().closeGame(deleteGameCommand.getGameId());
    getServer().getGameCache().queueDbDelete(deleteGameCommand.getGameId(), deleteGameCommand.isWithGamesInfo());
  }
  
}
