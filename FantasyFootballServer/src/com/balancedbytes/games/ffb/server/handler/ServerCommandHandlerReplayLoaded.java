package com.balancedbytes.games.ffb.server.handler;

import java.nio.channels.SocketChannel;

import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
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

  public void handleCommand(ReceivedCommand pReceivedCommand) {

    InternalServerCommandReplayLoaded replayCommand = (InternalServerCommandReplayLoaded) pReceivedCommand.getCommand();
    SocketChannel sender = pReceivedCommand.getSender();
    
    if (replayCommand.getGameId() > 0) {
      GameState gameState = getServer().getGameCache().getGameStateById(replayCommand.getGameId());
      if (gameState != null) {
        UtilReplay.startServerReplay(gameState, replayCommand.getReplayToCommandNr(), sender);
      } else {
        getServer().getCommunication().sendStatus(sender, ServerStatus.ERROR_UNKNOWN_GAME_ID, null);
      }
    }
    
  }
  
}
