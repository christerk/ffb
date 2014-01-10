package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandDeleteGame extends InternalServerCommand {
  
  public InternalServerCommandDeleteGame(long pGameId) {
    super(pGameId);
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_DELETE_GAME;
  }

}
