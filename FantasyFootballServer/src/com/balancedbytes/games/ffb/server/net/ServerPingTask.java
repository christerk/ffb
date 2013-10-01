package com.balancedbytes.games.ffb.server.net;

import java.nio.channels.SocketChannel;
import java.util.TimerTask;

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
      ChannelManager channelManager = getServer().getChannelManager();
      for (SocketChannel channel : channelManager.getAllChannels()) {
        long lastPing = channelManager.getLastPing(channel);
        if ((fMaxPingDelay > 0) && (lastPing > 0) && (currentTimeMillis - lastPing > fMaxPingDelay)) {
        	if (getServer().getDebugLog().isLogging(IServerLogLevel.WARN)) {
            StringBuilder logMessage = new StringBuilder();
            String coach = channelManager.getCoachForChannel(channel);
            GameState gameState = getServer().getGameCache().getGameStateById(channelManager.getGameIdForChannel(channel));
            ClientMode clientMode = channelManager.getModeForChannel(channel);
            logMessage.append("Connection closed for ");
            logMessage.append((ClientMode.PLAYER == clientMode) ? "Player " : "Spectator ");
            logMessage.append(coach);
            logMessage.append(" (Ping Timeout).");
            getServer().getDebugLog().log(IServerLogLevel.WARN, (gameState != null) ? gameState.getId() : -1, logMessage.toString());
        	}
          getServer().getNioServer().removeChannel(channel);  // sends an internalCommandSocketClosed
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
