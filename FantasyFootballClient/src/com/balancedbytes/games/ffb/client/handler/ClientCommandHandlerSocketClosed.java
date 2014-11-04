package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;

public class ClientCommandHandlerSocketClosed extends ClientCommandHandler {

  protected ClientCommandHandlerSocketClosed(FantasyFootballClient pClient) {
    super(pClient);
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_SOCKET_CLOSED;
  }

  public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
    
    // InternalCommandSocketClosed socketClosedCommand = (InternalCommandSocketClosed) pNetCommand;
    
    UserInterface userInterface = getClient().getUserInterface();
    userInterface.getStatusReport().reportSocketClosed();
    
    return true;

  }
  
}
