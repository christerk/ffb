package com.balancedbytes.games.ffb.client;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class GameTitle {
  
  private static final long _MILLISECONDS = 1;
  private static final long _SECONDS = 1000 * _MILLISECONDS;
  private static final long _MINUTES = 60 * _SECONDS;
  private static final long _HOURS = 60 * _MINUTES;
  private static final long _DAYS = 24 * _HOURS;
  
  private ClientMode fClientMode;
  private boolean fTesting;
  private String fHomeCoach;
  private String fAwayCoach;
  private long fPingTime;
  private long fTurnTime;
  private long fGameTime;
  
  public GameTitle() {
    setPingTime(-1);
  }
  
  public GameTitle(GameTitle pGameTitle) {
    if (pGameTitle != null) {
      setClientMode(pGameTitle.getClientMode());
      setTesting(pGameTitle.isTesting());
      setHomeCoach(pGameTitle.getHomeCoach());
      setAwayCoach(pGameTitle.getAwayCoach());
      setPingTime(pGameTitle.getPingTime());
      setTurnTime(pGameTitle.getTurnTime());
      setGameTime(pGameTitle.getGameTime());
    }
  }
  
  public void setTesting(boolean pTesting) {
    fTesting = pTesting;
  }
  
  public boolean isTesting() {
    return fTesting;
  }
  
  public ClientMode getClientMode() {
    return fClientMode;
  }
  
  public void setClientMode(ClientMode pClientMode) {
    fClientMode = pClientMode;
  }
  
  public String getHomeCoach() {
    return fHomeCoach;
  }
  
  public void setHomeCoach(String pHomeCoach) {
    fHomeCoach = pHomeCoach;
  }
  
  public String getAwayCoach() {
    return fAwayCoach;
  }
  
  public void setAwayCoach(String pAwayCoach) {
    fAwayCoach = pAwayCoach;
  }
  
  public long getPingTime() {
    return fPingTime;
  }
  
  public void setPingTime(long pPingTime) {
    fPingTime = pPingTime;
  }
  
  public long getTurnTime() {
    return fTurnTime;
  }
  
  public void setTurnTime(long pTurnTime) {
    fTurnTime = pTurnTime;
  }
  
  public long getGameTime() {
    return fGameTime;
  }
  
  public void setGameTime(long pGameTime) {
    fGameTime = pGameTime;
  }

  public String toString() {
    StringBuilder title = new StringBuilder();
    title.append("FantasyFootball");
    if (StringTool.isProvided(getHomeCoach()) && StringTool.isProvided(getAwayCoach())) {
      if (isTesting()) {
        title.append(" test ");
      } else {
        if (ClientMode.PLAYER == getClientMode()) {
          title.append(" - ");
        }
        if (ClientMode.SPECTATOR == getClientMode()) {
          title.append(" spectate ");
        }
        if (ClientMode.REPLAY == getClientMode()) {
          title.append(" replay ");
        }
      }
      title.append(getHomeCoach()).append(" vs ").append(getAwayCoach());
    }
    if ((ClientMode.REPLAY != getClientMode()) && (getTurnTime() >= 0)) {
      title.append(" - Turn ");
      appendTime(title, getTurnTime(), false);
    }
    if (getGameTime() >= 0) {
      title.append(" - Game ");
      appendTime(title, getGameTime(), true);
    }
    if (getPingTime() >= 0) {
      title.append(" - Ping ");
      title.append(getPingTime()).append("ms");
    }
    return title.toString();
  }
  
  private void appendTime(StringBuilder pBuffer, long pMilliseconds, boolean pShowHours) {
    
    long milliseconds = pMilliseconds;
    
    int days = 0;
    if (milliseconds >= _DAYS) {
      days = (int) (milliseconds / _DAYS);
      milliseconds -= days * _DAYS;
      pBuffer.append(days).append("d");
    }
    
    int hours = 0;
    if (pShowHours || (days > 0) || (milliseconds >= _HOURS)) {
      hours = (int) (milliseconds / _HOURS);
      milliseconds -= hours * _HOURS;
      appendMin2Digits(pBuffer, hours).append("h");
    }
    
    int minutes = (int) (milliseconds / _MINUTES);
    milliseconds -= minutes * _MINUTES;
    appendMin2Digits(pBuffer, minutes).append("m");
    
    int seconds = (int) (milliseconds / _SECONDS);
    milliseconds -= seconds * _SECONDS;
    appendMin2Digits(pBuffer, seconds).append("s");

  }
  
  private StringBuilder appendMin2Digits(StringBuilder pBuffer, int pValue) {
    if (pValue < 10) {
      pBuffer.append("0");
    }
    pBuffer.append(pValue);
    return pBuffer;
  }
  
}
