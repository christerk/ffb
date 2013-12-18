package com.balancedbytes.games.ffb.server.handler;

import java.nio.channels.SocketChannel;

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

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    
    ClientCommandPing pingCommand = (ClientCommandPing) pReceivedCommand.getCommand();
    SocketChannel sender = pReceivedCommand.getSender();

    getServer().getCommunication().sendPing(sender, pingCommand.getTimestamp());
    getServer().getChannelManager().setLastPing(sender, System.currentTimeMillis());
    if (pingCommand.hasEntropy()) {
      getServer().getFortuna().addEntropy(pingCommand.getEntropy());
    }
    
  }

}
