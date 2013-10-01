package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.GameTitle;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPing;

public class ClientCommandHandlerPing extends ClientCommandHandler {

  protected ClientCommandHandlerPing(FantasyFootballClient pClient) {
    super(pClient);
  }

  public NetCommandId getId() {
    return NetCommandId.SERVER_PING;
  }

  public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
    
    ServerCommandPing pingCommand = (ServerCommandPing) pNetCommand;
    
    UserInterface userInterface = getClient().getUserInterface();
    GameTitle gameTitle = new GameTitle(userInterface.getGameTitle());
    gameTitle.setPingTime(pingCommand.getReceived() - pingCommand.getTimestamp());
    updateGameTitle(gameTitle);
    
    return true;

  }
  
}
