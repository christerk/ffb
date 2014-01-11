package com.balancedbytes.games.ffb.client.net;

import java.util.TimerTask;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.fumbbl.rng.MouseEntropySource;

public class ClientPingTask extends TimerTask {
  
  private static final boolean _TRACE = false;
  
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
    if (getClient().getCommandSocket().isOpen()) {
      long currentTimeMillis = System.currentTimeMillis();
      long lastPingReceived = getLastPingReceived();
      if (_TRACE) {
        if (lastPingReceived > 0) {
          System.out.println("Ping Delay: " + (currentTimeMillis - getLastPingReceived()));
        }
      }
      if ((fMaxDelayPing > 0) && (lastPingReceived > 0) && (currentTimeMillis - lastPingReceived > fMaxDelayPing)) {
        getClient().getUserInterface().getStatusReport().reportServerUnreachable();
        getClient().stopClient();
      } else {
        MouseEntropySource mouseEntropySource = getClient().getUserInterface().getMouseEntropySource();
        boolean hasEnoughEntropy = mouseEntropySource.hasEnoughEntropy();
        byte entropy = mouseEntropySource.getEntropy();
        getClient().getCommunication().sendPing(currentTimeMillis, hasEnoughEntropy, entropy);
        // System.out.println("Ping (" + currentTimeMillis + "," + hasEnoughEntropy + "," + entropy + ")");
      }
    }
  }
  
  public FantasyFootballClient getClient() {
    return fClient;
  }
  
}
