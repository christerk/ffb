package com.balancedbytes.games.ffb.server.net;

import java.util.TimerTask;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.fumbbl.rng.NetworkEntropySource;

public class ServerPingTask extends TimerTask {
  
  private FantasyFootballServer fServer;
  private int fPingInterval; 
  private int fMaxPingDelay;
  private int fDbKeepAlive;
  private int fDbKeepAliveCounter;
  private NetworkEntropySource fNetworkEntropySource;
  
  public ServerPingTask(FantasyFootballServer pServer, int pPingInterval, int pMaxPingDelay, int pDbKeepAlive) {
    fServer = pServer;
    fPingInterval = pPingInterval;
    fMaxPingDelay = pMaxPingDelay;
    fDbKeepAlive = pDbKeepAlive;
    fDbKeepAliveCounter = 0;
    // The NetworkEntropySource defaults to pinging localhost.
    // Here we add some more hosts. This should be configurable.
    fNetworkEntropySource = new NetworkEntropySource();
    fNetworkEntropySource.addEndpoint("www.google.com");
    fNetworkEntropySource.addEndpoint("slashdot.org");
    fNetworkEntropySource.addEndpoint("192.168.0.1");
  }

  public void run() {
    try {
      long currentTimeMillis = System.currentTimeMillis();
      SessionManager sessionManager = getServer().getSessionManager();
      for (Session session : sessionManager.getAllSessions()) {
        long lastPing = sessionManager.getLastPing(session);
        if ((fMaxPingDelay > 0) && (lastPing > 0) && (currentTimeMillis - lastPing > fMaxPingDelay)) {
        	if (getServer().getDebugLog().isLogging(IServerLogLevel.WARN)) {
            StringBuilder logMessage = new StringBuilder();
            String coach = sessionManager.getCoachForSession(session);
            GameState gameState = getServer().getGameCache().getGameStateById(sessionManager.getGameIdForSession(session));
            ClientMode clientMode = sessionManager.getModeForSession(session);
            logMessage.append("Connection closed for ");
            logMessage.append((ClientMode.PLAYER == clientMode) ? "Player " : "Spectator ");
            logMessage.append(coach);
            logMessage.append(" (Ping Timeout).");
            getServer().getDebugLog().log(IServerLogLevel.WARN, (gameState != null) ? gameState.getId() : -1, logMessage.toString());
        	}
        	getServer().getCommunication().close(session);  // sends an internalCommandSocketClosed
        }
      }
      if (fNetworkEntropySource.hasEnoughEntropy()) {
        getServer().getFortuna().addEntropy(fNetworkEntropySource.getEntropy());
      }
      if (fDbKeepAlive > 0) {
        fDbKeepAliveCounter += fPingInterval;
        if (fDbKeepAliveCounter >= fDbKeepAlive) {
          getServer().getDbConnectionManager().doKeepAlivePing();
          fDbKeepAliveCounter = 0;
        }
      }
    } catch (Exception pException) {
      getServer().getDebugLog().log(pException);
      System.exit(99);
    }
  }
  
  public FantasyFootballServer getServer() {
    return fServer;
  }

}
