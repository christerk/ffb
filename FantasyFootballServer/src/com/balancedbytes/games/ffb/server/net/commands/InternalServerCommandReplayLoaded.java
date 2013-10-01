package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandReplayLoaded extends InternalServerCommand {
  
  private int fReplayToCommandNr;
  
  public InternalServerCommandReplayLoaded(long pGameId, int pReplayToCommandNr) {
    super(pGameId);
    fReplayToCommandNr = pReplayToCommandNr;
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_REPLAY_LOADED;
  }
 
  public int getReplayToCommandNr() {
    return fReplayToCommandNr;
  }

}
