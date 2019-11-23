package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPing;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerPing extends ServerCommandHandler {

  protected ServerCommandHandlerPing(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_PING;
  }

  public boolean handleCommand(ReceivedCommand pReceivedCommand) {
    ClientCommandPing pingCommand = (ClientCommandPing) pReceivedCommand.getCommand();
    getServer().getSessionManager().setLastPing(pReceivedCommand.getSession(), System.currentTimeMillis());
    getServer().getCommunication().sendPong(pReceivedCommand.getSession(), pingCommand.getTimestamp());
    return true;
  }

}
