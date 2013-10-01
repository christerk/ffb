package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.INetCommandHandler;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;

/**
 * 
 * @author Kalimar
 */
public abstract class ServerCommandHandler implements INetCommandHandler {
  
  private FantasyFootballServer fServer;
  
  protected ServerCommandHandler(FantasyFootballServer pServer) {
    fServer = pServer;
  }

  public abstract NetCommandId getId();
  
  public abstract void handleNetCommand(NetCommand pNetCommand);
  
  protected FantasyFootballServer getServer() {
    return fServer;
  }

}
