package com.balancedbytes.games.ffb.server.net;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class CommandServlet extends WebSocketServlet implements WebSocketCreator {
  
  private FantasyFootballServer fServer;
  
  public CommandServlet(FantasyFootballServer pServer) {
    fServer = pServer;
  }
  
  @Override
  public void configure(WebSocketServletFactory factory) {
    factory.getPolicy().setIdleTimeout(10000);
    factory.setCreator(this);
  }
  
  public Object createWebSocket(ServletUpgradeRequest pRequest, ServletUpgradeResponse pResponse) {
    return new CommandSocket(fServer.getCommunication());
  }
  
}