package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandReplayLoaded;
import com.balancedbytes.games.ffb.server.util.UtilServerReplay;

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

  public void handleCommand(ReceivedCommand pReceivedCommand) {

    InternalServerCommandReplayLoaded replayCommand = (InternalServerCommandReplayLoaded) pReceivedCommand.getCommand();
    
    if (replayCommand.getGameId() > 0) {
      GameState gameState = getServer().getGameCache().getGameStateById(replayCommand.getGameId());
      if (gameState != null) {
        UtilServerReplay.startServerReplay(gameState, replayCommand.getReplayToCommandNr(), pReceivedCommand.getSession());
      } else {
        getServer().getCommunication().sendStatus(pReceivedCommand.getSession(), ServerStatus.ERROR_UNKNOWN_GAME_ID, null);
      }
    }
    
  }
  
}
