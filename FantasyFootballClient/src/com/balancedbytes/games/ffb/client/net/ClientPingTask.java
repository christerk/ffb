package com.balancedbytes.games.ffb.client.net;

import java.util.TimerTask;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;

public class ClientPingTask extends TimerTask {
  
  private FantasyFootballClient fClient;
  private int fMaxDelayPing;
  private long fLastPingReceived;
  
  public ClientPingTask(FantasyFootballClient pClient, int pMaxDelayPing) {
    fClient = pClient;
    fMaxDelayPing = pMaxDelayPing;
  }
  
  public int getMaxDelayPing() {
    return fMaxDelayPing;
  }

  public void setLastPingReceived(long pTimeMillis) {
    synchronized (this) {
      fLastPingReceived = pTimeMillis;
    }
  }
  
  public long getLastPingReceived() {
    synchronized (this) {
      return fLastPingReceived;
    }
  }
  
  public void run() {
    if (getClient().getCommandEndpoint().isOpen()) {
      long currentTimeMillis = System.currentTimeMillis();
      long lastPingReceived = getLastPingReceived();
      if ((fMaxDelayPing > 0) && (lastPingReceived > 0) && (currentTimeMillis - lastPingReceived > fMaxDelayPing)) {
        getClient().getUserInterface().getStatusReport().reportServerUnreachable();
        getClient().stopClient();
      } else {
        getClient().getCommunication().sendPing(currentTimeMillis);
      }
    }
  }
  
  public FantasyFootballClient getClient() {
    return fClient;
  }
  
}
