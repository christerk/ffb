package com.balancedbytes.games.ffb.server.net;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandFactory;
import com.balancedbytes.games.ffb.server.handler.IReceivedCommandHandler;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandSocketClosed;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class CommandSocket {
  
  private IReceivedCommandHandler fCommandHandler;
  private NetCommandFactory fNetCommandFactory;
    
  public CommandSocket(IReceivedCommandHandler pCommandHandler) {
    fCommandHandler = pCommandHandler;
    fNetCommandFactory = new NetCommandFactory();
  }

  @OnWebSocketMessage
  public void onTextMessage(Session pSession, String pTextMessage) {
    
    if ((pSession == null) || (pTextMessage == null) || !pSession.isOpen()) {
      return;
    }
    
    // inflate from base64 if necessary
    JsonValue jsonValue;
    try {
      jsonValue = UtilJson.inflateFromBase64(pTextMessage);
    } catch (IOException pIoException) {
      jsonValue = null;
    }
        
    NetCommand netCommand = fNetCommandFactory.forJsonValue(jsonValue);
    if (netCommand == null) {
      return;
    }
    
    ReceivedCommand receivedCommand = new ReceivedCommand(netCommand, pSession);
    fCommandHandler.handleCommand(receivedCommand);
    
  }
  
  @OnWebSocketConnect
  public void onConnect(Session pSession) {
    pSession.setIdleTimeout(Long.MAX_VALUE);
  }
  
  @OnWebSocketClose
  public void onClose(Session pSession, int pCloseCode, String pCloseReason) {
    if (pSession == null)  {
      return;
    }
    fCommandHandler.handleCommand(new ReceivedCommand(new InternalServerCommandSocketClosed(), pSession));
  }
  
}
