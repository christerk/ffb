package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandBackupGame extends InternalServerCommand {
  
  public InternalServerCommandBackupGame(long pGameId) {
    super(pGameId);
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_BACKUP_GAME;
  }

}
