package org.samples.websockets.embeddingjetty;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket(maxMessageSize=64*1024)
public class ChatWebSocket {
  
  private static final Set<Session> sSessions = new CopyOnWriteArraySet<Session>();

  @OnWebSocketConnect
  public void onConnect(Session pSession) {
    sSessions.add(pSession);
  }
  
  @OnWebSocketMessage
  public void onMessage(String pMessage) {
    for (Session session : sSessions) {
      try {
        session.getRemote().sendString(pMessage);
      } catch (IOException pIoExceptionOnSend) {
        try {
          session.disconnect();
        } catch (IOException pIoExceptionOnDisconnect) {
          // nothing to be done
        }
        sSessions.remove(session);
      }
    }
  }
  
  @OnWebSocketClose
  public void onClose(Session pSession, int pStatusCode, String pReason) {
    sSessions.remove(pSession);
  }

}