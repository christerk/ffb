package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandPing;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;

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

  public void handleNetCommand(NetCommand pNetCommand) {
    ClientCommandPing pingCommand = (ClientCommandPing) pNetCommand;
    getServer().getCommunication().sendPing(pingCommand.getSender(), pingCommand.getTimestamp());
    getServer().getChannelManager().setLastPing(pingCommand.getSender(), System.currentTimeMillis());
    if (pingCommand.hasEntropy()) {
      getServer().getFortuna().addEntropy(pingCommand.getEntropy());
    }
  }

}
