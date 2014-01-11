package com.balancedbytes.games.ffb.server.net;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public class SessionManager {
  
  private Map<Long, Set<Session>> fSessionsByGameId;
  private Map<Session, JoinedClient> fClientBySession;
  private Map<Session, Long> fLastPingBySession;
  
  private class JoinedClient {
    
    private long fGameId;
    private String fCoach;
    private ClientMode fMode;
    private boolean fHomeCoach;
    
    public JoinedClient(long pGameId, String pCoach, ClientMode pMode, boolean pHomeCoach) {
      fGameId = pGameId;
      fCoach = pCoach;
      fMode = pMode;
      fHomeCoach = pHomeCoach;
    }
    
    public long getGameId() {
      return fGameId;
    }
    
    public String getCoach() {
      return fCoach;
    }
    
    public ClientMode getMode() {
      return fMode;
    }
    
    public boolean isHomeCoach() {
      return fHomeCoach;
    }
    
  }
  
  public SessionManager() {
    fSessionsByGameId = new HashMap<Long, Set<Session>>();
    fClientBySession = new HashMap<Session, JoinedClient>();
    fLastPingBySession = new HashMap<Session, Long>();
  }
  
  public long getGameIdForSession(Session pSession) {
    JoinedClient client = fClientBySession.get(pSession);
    if (client != null) {
      return client.getGameId();
    } else {
      return 0;
    }
  }
  
  public String getCoachForSession(Session pSession) {
    JoinedClient client = fClientBySession.get(pSession);
    if (client != null) {
      return client.getCoach();
    } else {
      return null;
    }
  }
  
  public ClientMode getModeForSession(Session pSession) {
    JoinedClient client = fClientBySession.get(pSession);
    if (client != null) {
      return client.getMode();
    } else {
      return null;
    }
  }
  
  public Session[] getSessionsForGameId(long pGameId) {
    Set<Session> sessions = fSessionsByGameId.get(pGameId);
    if (sessions != null) {
      return (Session[]) sessions.toArray(new Session[sessions.size()]);
    } else {
      return new Session[0];
    }
  }
  
  public Session getSessionOfHomeCoach(GameState pGameState) {
    Session sessionHomeCoach = null;
    if (pGameState != null) {
      Set<Session> sessions = fSessionsByGameId.get(pGameState.getId());
      if (sessions != null) {
        Iterator<Session> sessionIterator = sessions.iterator();
        while ((sessionHomeCoach == null) && sessionIterator.hasNext()) {
          Session session = sessionIterator.next();
          JoinedClient client = fClientBySession.get(session);
          if ((client != null) && (client.getMode() == ClientMode.PLAYER) && client.isHomeCoach()) {
            sessionHomeCoach = session;            
          }
        }
      }
    }
    return sessionHomeCoach;
  }

  public boolean isHomeCoach(GameState pGameState, String pCoach) {
    JoinedClient clientHomeCoach = fClientBySession.get(getSessionOfHomeCoach(pGameState));
    return ((clientHomeCoach != null) && clientHomeCoach.getCoach().equals(pCoach));
  }
  
  public Session getSessionOfAwayCoach(GameState pGameState) {
    Session sessionAwayCoach = null;
    if (pGameState != null) {
      Set<Session> sessions = fSessionsByGameId.get(pGameState.getId());
      if (sessions != null) {
        Iterator<Session> sessionIterator = sessions.iterator();
        while ((sessionAwayCoach == null) && sessionIterator.hasNext()) {
          Session session = sessionIterator.next();
          JoinedClient client = fClientBySession.get(session);
          if ((client != null) && (client.getMode() == ClientMode.PLAYER) && !client.isHomeCoach()) {
            sessionAwayCoach = session;            
          }
        }
      }
    }
    return sessionAwayCoach;
  }

  public boolean isAwayCoach(GameState pGameState, String pCoach) {
    JoinedClient clientAwayCoach = fClientBySession.get(getSessionOfAwayCoach(pGameState));
    return ((clientAwayCoach != null) && clientAwayCoach.getCoach().equals(pCoach));
  }

  public Session[] getSessionsWithoutAwayCoach(GameState pGameState) {
    Session[] filteredSessions = new Session[0];
    if (pGameState != null) {
      Set<Session> sessions = fSessionsByGameId.get(pGameState.getId());
      if ((sessions != null) && (sessions.size() > 0)) {
        Session sessionAwayCoach = getSessionOfAwayCoach(pGameState);
        if (sessionAwayCoach != null) {
          int i = 0;
          filteredSessions = new Session[sessions.size() - 1];
          Iterator<Session> sessionIterator = sessions.iterator();
          while (sessionIterator.hasNext()) {
            Session session = sessionIterator.next();
            if (session != sessionAwayCoach) {
              filteredSessions[i] = session;
              i++;
            }
          }
        }
      }
    }
    return filteredSessions;
  }

  public Session[] getSessionsWithoutHomeCoach(GameState pGameState) {
    Session[] filteredSessions = new Session[0];
    if (pGameState != null) {
      Set<Session> sessions = fSessionsByGameId.get(pGameState.getId());
      if ((sessions != null) && (sessions.size() > 0)) {
        Session sessionHomeCoach = getSessionOfHomeCoach(pGameState);
        if (sessionHomeCoach != null) {
          int i = 0;
          filteredSessions = new Session[sessions.size() - 1];
          Iterator<Session> sessionIterator = sessions.iterator();
          while (sessionIterator.hasNext()) {
            Session session = sessionIterator.next();
            if (session != sessionHomeCoach) {
              filteredSessions[i] = session;
              i++;
            }
          }
        }
      }
    }
    return filteredSessions;
  }

  public Session[] getSessionsOfSpectators(GameState pGameState) {
    Session[] filteredSessions = new Session[0];
    if (pGameState != null) {
      Set<Session> sessions = fSessionsByGameId.get(pGameState.getId());
      if ((sessions != null) && (sessions.size() > 0)) {
        Session sessionAwayCoach = getSessionOfAwayCoach(pGameState);
        Session sessionHomeCoach = getSessionOfHomeCoach(pGameState);
        if ((sessionHomeCoach != null) && (sessionAwayCoach != null)) {
          int i = 0;
          filteredSessions = new Session[sessions.size() - 2];
          Iterator<Session> sessionIterator = sessions.iterator();
          while (sessionIterator.hasNext()) {
            Session session = sessionIterator.next();
            if ((session != sessionAwayCoach) && (session != sessionHomeCoach)) {
              filteredSessions[i] = session;
              i++;
            }
          }
        }
      }
    }
    return filteredSessions;
  }

  public void addSession(Session pSession, GameState pGameState, String pCoach, ClientMode pMode, boolean pHomeCoach) {
    if (pGameState != null) {
      JoinedClient client = new JoinedClient(pGameState.getId(), pCoach, pMode, pHomeCoach);
      fClientBySession.put(pSession, client);
      Set<Session> sessions = fSessionsByGameId.get(pGameState.getId());
      if (sessions == null) {
        sessions = new HashSet<Session>();
        fSessionsByGameId.put(pGameState.getId(), sessions);
      }
      sessions.add(pSession);
    }
  }
  
  public void removeSession(Session pSession) {
    long gameId = getGameIdForSession(pSession);
    fClientBySession.remove(pSession);
    Set<Session> sessions = fSessionsByGameId.get(gameId);
    if (sessions != null) {
      sessions.remove(pSession);
      if (sessions.size() == 0) {
        fSessionsByGameId.remove(gameId);
      }
    }
  }

  public void setLastPing(Session pSession, long pPing) {
    fLastPingBySession.put(pSession, pPing);
  }
  
  public long getLastPing(Session pSession) {
    Long lastPing = fLastPingBySession.get(pSession);
    return (lastPing != null) ? lastPing : 0;
  }
  
  public Session[] getAllSessions() {
    synchronized (fClientBySession) {
      return fClientBySession.keySet().toArray(new Session[fClientBySession.size()]);
    }
  }
  
}
