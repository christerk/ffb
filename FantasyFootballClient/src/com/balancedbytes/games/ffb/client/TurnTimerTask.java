package com.balancedbytes.games.ffb.client;

import java.util.TimerTask;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.client.ui.GameTitleUpdateTask;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.old.GameOptionOld;

/**
 * 
 * @author Kalimar
 */
public class TurnTimerTask extends TimerTask {
  
  private FantasyFootballClient fClient;
  
  public TurnTimerTask(FantasyFootballClient pClient) {
    fClient = pClient;
  }

  public void run() {
    Game game = getClient().getGame();
    UserInterface userInterface = getClient().getUserInterface();
    ClientData clientData = getClient().getClientData();
    GameTitle gameTitle = new GameTitle(userInterface.getGameTitle());
    if (game.getStarted() != null) {
      if (game.getFinished() == null) {
        game.setGameTime(game.getGameTime() + 1000);
        gameTitle.setGameTime(game.getGameTime());
      }
    } else {
      gameTitle.setGameTime(-1);
    }
    if (game.isTurnTimeEnabled() && !clientData.isTurnTimerStopped()) {
      if (!game.isWaitingForOpponent()) {
        game.setTurnTime(game.getTurnTime() + 1000);
      }
      gameTitle.setTurnTime(game.getTurnTime());
    } else {
      gameTitle.setTurnTime(-1);
    }
    userInterface.invokeAndWait(new GameTitleUpdateTask(getClient(), gameTitle));
    if ((ClientMode.PLAYER == getClient().getMode()) && game.isTurnTimeEnabled() && !clientData.isTurnTimerStopped() && !game.isHomePlaying() && !game.isTimeoutPossible() && !game.isTimeoutEnforced() && (game.getOptions().getOptionValue(GameOptionOld.TURNTIME).getValue() > 0) && (game.getTurnTime() >= game.getOptions().getOptionValue(GameOptionOld.TURNTIME).getValue() * 1000)) {
      getClient().getCommunication().sendTimeoutPossible();
    }
  }
  
  public FantasyFootballClient getClient() {
    return fClient;
  }
  
}
