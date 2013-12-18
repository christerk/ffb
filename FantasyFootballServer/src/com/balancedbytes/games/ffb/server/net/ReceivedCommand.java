package com.balancedbytes.games.ffb.server.net;

import java.nio.channels.SocketChannel;

import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ReceivedCommand {
  
  private NetCommand fCommand;
  private SocketChannel fSender;
  
  public ReceivedCommand(NetCommand pCommand) {
    this(pCommand, null);
  }

  public ReceivedCommand(NetCommand pCommand, SocketChannel pSender) {
    fCommand = pCommand;
    setSender(pSender);
  }

  public NetCommand getCommand() {
    return fCommand;
  }
  
  public SocketChannel getSender() {
    return fSender;
  }
  
  public void setSender(SocketChannel pSender) {
    fSender = pSender;
  }
  
  // convenience methods ...
  
  public NetCommandId getId() {
    return getCommand().getId();
  }
  
  public boolean isInternal() {
    return getCommand().isInternal();
  }

}
