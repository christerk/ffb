package com.balancedbytes.games.ffb.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
// @WebServlet(name = "Simple WebSocket Servlet", urlPatterns = { "/echo" })
public class SimpleEchoServlet extends WebSocketServlet {

  @Override
  public void configure(WebSocketServletFactory factory) {
    factory.getPolicy().setIdleTimeout(10000);
    factory.register(SimpleEchoSocket.class);
  }

}