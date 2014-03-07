package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;

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
    
    long gameId = getServer().getSessionManager().getGameIdForSession(pReceivedCommand.getSession());
    GameState gameState = getServer().getGameCache().getGameStateById(gameId);
    Game game = (gameState != null) ? gameState.getGame() : null;
    
    if ((game != null) && !game.isTimeoutPossible() && !game.isTimeoutEnforced()) {
      long clientTurnLimit = UtilGameOption.getIntOption(game, GameOptionId.TURNTIME) * 1000;
      long turnTime = System.currentTimeMillis() - gameState.getTurnTimeStarted();
      if (turnTime >= clientTurnLimit - 1000) {
        game.setTimeoutPossible(true);
        UtilServerGame.syncGameModel(gameState, null, null, Sound.WHISTLE);
      }
    }
    
  }

}
