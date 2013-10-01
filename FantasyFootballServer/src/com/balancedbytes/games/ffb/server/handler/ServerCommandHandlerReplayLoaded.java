package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandReplayLoaded;
import com.balancedbytes.games.ffb.server.util.UtilReplay;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerReplayLoaded extends ServerCommandHandler {

  protected ServerCommandHandlerReplayLoaded(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_REPLAY_LOADED;
  }

  public void handleNetCommand(NetCommand pNetCommand) {

    InternalServerCommandReplayLoaded replayCommand = (InternalServerCommandReplayLoaded) pNetCommand;
    
    if (replayCommand.getGameId() > 0) {
      GameState gameState = getServer().getGameCache().getGameStateById(replayCommand.getGameId());
      if (gameState != null) {
        UtilReplay.startServerReplay(gameState, replayCommand.getReplayToCommandNr(), replayCommand.getSender());
      } else {
        getServer().getCommunication().sendStatus(replayCommand.getSender(), ServerStatus.ERROR_UNKNOWN_GAME_ID, null);
      }
    }
    
  }
  
}
