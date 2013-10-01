package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.net.NetCommandId;


/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandUploadGame extends InternalServerCommand {

	private String fConcedingTeamId;
	
  public InternalServerCommandUploadGame(long pGameId) {
    this(pGameId, null);
  }

  public InternalServerCommandUploadGame(long pGameId, String pConcedingTeamId) {
    super(pGameId);
    fConcedingTeamId = pConcedingTeamId;
  }

  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_UPLOAD_GAME;
  }
  
  public String getConcedingTeamId() {
	  return fConcedingTeamId;
  }
  
}
