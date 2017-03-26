package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerClearCache extends ServerCommandHandler {

  protected ServerCommandHandlerClearCache(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_CLEAR_CACHE;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    getServer().getGameCache().clearCache();
  }
  
}
