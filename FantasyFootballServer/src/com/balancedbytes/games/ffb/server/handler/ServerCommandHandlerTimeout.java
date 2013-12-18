package com.balancedbytes.games.ffb.server.handler;

import java.nio.channels.SocketChannel;

import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.util.UtilGame;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerTimeout extends ServerCommandHandler {

  protected ServerCommandHandlerTimeout(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_TIMEOUT_POSSIBLE;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    
    SocketChannel sender = pReceivedCommand.getSender();
    
    long gameId = getServer().getChannelManager().getGameIdForChannel(sender);
    GameState gameState = getServer().getGameCache().getGameStateById(gameId);
    Game game = (gameState != null) ? gameState.getGame() : null;
    
    if ((game != null) && !game.isTimeoutPossible() && !game.isTimeoutEnforced()) {
      long clientTurnLimit = game.getOptions().getOptionValue(GameOption.TURNTIME).getValue() * 1000;
      long turnTime = System.currentTimeMillis() - gameState.getTurnTimeStarted();
      if (turnTime >= clientTurnLimit - 1000) {
        game.setTimeoutPossible(true);
        UtilGame.syncGameModel(gameState, null, null, Sound.WHISTLE);
      }
    }
    
  }

}
