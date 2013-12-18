package com.balancedbytes.games.ffb.server.net;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.balancedbytes.games.ffb.net.INetCommandHandler;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandFactory;
import com.eclipsesource.json.JsonValue;

@WebSocket(maxMessageSize = 64 * 1024)
public class CommandSocket {
  
  private INetCommandHandler fCommandHandler;
  private NetCommandFactory fNetCommandFactory;
    
  public CommandSocket(INetCommandHandler pCommandHandler) {
    fCommandHandler = pCommandHandler;
    fNetCommandFactory = new NetCommandFactory();
  }

  @OnWebSocketMessage
  public void onMessage(Session pSession, String pMessage) {
    if (pSession.isOpen()) {
      JsonValue jsonValue = JsonValue.readFrom(pMessage);
      NetCommand netCommand = fNetCommandFactory.forJsonValue(jsonValue);
      if (netCommand != null) {
        // netCommand.setSender(socketChannel);
        fCommandHandler.handleNetCommand(netCommand);
      }
    }
  }

}
