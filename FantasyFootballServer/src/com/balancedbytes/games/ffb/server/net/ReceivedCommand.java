package com.balancedbytes.games.ffb.server.net;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ReceivedCommand {
  
  private NetCommand fCommand;
  private Session fSession;
  
  public ReceivedCommand(NetCommand pCommand, Session pSession) {
    fCommand = pCommand;
    fSession = pSession;
  }

  public NetCommand getCommand() {
    return fCommand;
  }
  
  public Session getSession() {
    return fSession;
  }
  
  // convenience methods ...
  
  public NetCommandId getId() {
    return getCommand().getId();
  }
  
  public boolean isInternal() {
    return getCommand().isInternal();
  }

}
