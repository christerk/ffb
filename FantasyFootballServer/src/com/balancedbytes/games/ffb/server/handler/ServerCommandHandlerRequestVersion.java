package com.balancedbytes.games.ffb.server.handler;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerRequestVersion extends ServerCommandHandler {

  protected ServerCommandHandlerRequestVersion(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_REQUEST_VERSION;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    SocketChannel sender = pReceivedCommand.getSender();
    String[] properties = getServer().getProperties();
    List<String> clientProperties = new ArrayList<String>();
    List<String> clientPropertyValues = new ArrayList<String>();
    for (String property : properties) {
      if (property.startsWith("client.")) {
        clientProperties.add(property);
        clientPropertyValues.add(getServer().getProperty(property));
      }
    }
    getServer().getCommunication().sendVersion(
      sender,
      FantasyFootballServer.SERVER_VERSION,
      FantasyFootballServer.CLIENT_VERSION,
      clientProperties.toArray(new String[clientProperties.size()]),
      clientPropertyValues.toArray(new String[clientPropertyValues.size()])
    );
  }

}
