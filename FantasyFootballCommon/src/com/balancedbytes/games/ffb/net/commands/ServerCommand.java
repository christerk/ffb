package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.net.NetCommand;




/**
 * 
 * @author Kalimar
 */
public abstract class ServerCommand extends NetCommand {

  protected static final String XML_ATTRIBUTE_COMMAND_NR = "cmdNr";
  
  private int fCommandNr;
  
  public int getCommandNr() {
    return fCommandNr;
  }
  
  public void setCommandNr(int pCommandNr) {
    fCommandNr = pCommandNr;
  }
  
  public boolean isReplayable() {
    return true;
  }
    
}
