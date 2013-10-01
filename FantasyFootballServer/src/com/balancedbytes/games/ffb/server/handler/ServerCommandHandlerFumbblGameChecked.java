package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandFumbblGameChecked;
import com.balancedbytes.games.ffb.server.util.UtilStartGame;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerFumbblGameChecked extends ServerCommandHandler {
	
  protected ServerCommandHandlerFumbblGameChecked(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_FUMBBL_GAME_CHECKED;
  }

  public void handleNetCommand(NetCommand pNetCommand) {
    
  	InternalServerCommandFumbblGameChecked gameCheckedCommand = (InternalServerCommandFumbblGameChecked) pNetCommand;
    GameState gameState = getServer().getGameCache().getGameStateById(gameCheckedCommand.getGameId());
    UtilStartGame.startGame(gameState);
    
  }

}
