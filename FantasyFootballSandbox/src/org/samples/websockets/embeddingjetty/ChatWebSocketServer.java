package org.samples.websockets.embeddingjetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ChatWebSocketServer {

  public static void main(String[] args) {
    
    try {

      Server server = new Server(8081);
      WebSocketHandler wsHandler = new WebSocketHandler() {
        @Override
        public void configure(WebSocketServletFactory factory) {
          factory.register(ChatWebSocket.class);
        }
      };
      ContextHandler context = new ContextHandler();
      context.setContextPath("/chat");
      context.setHandler(wsHandler);
      server.setHandler(context);
      server.start();
      server.join();

    } catch (Throwable e) {
      e.printStackTrace();
    }
    
  }

}