package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.GameTitle;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPong;

public class ClientCommandHandlerPong extends ClientCommandHandler {

  protected ClientCommandHandlerPong(FantasyFootballClient pClient) {
    super(pClient);
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_PONG;
  }

  public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
    
    ServerCommandPong pongCommand = (ServerCommandPong) pNetCommand;
    
    if ((pongCommand.getReceived() > 0) && (pongCommand.getTimestamp() > 0)) {
      getClient().getClientPingTask().setLastPingReceived(pongCommand.getReceived());
      GameTitle gameTitle = new GameTitle();
      gameTitle.setPingTime(pongCommand.getReceived() - pongCommand.getTimestamp());
      // System.out.println("Ping Time " + gameTitle.getPingTime() + "ms");
      updateGameTitle(gameTitle);
    }
    
    return true;

  }
  
}
