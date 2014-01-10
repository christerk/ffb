package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandFumbblGameCreated extends InternalServerCommand {
  
  public InternalServerCommandFumbblGameCreated(long pGameId) {
    super(pGameId);
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_FUMBBL_GAME_CREATED;
  }
   
}
