package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.util.UtilCursor;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilPassing;

/**
 * 
 * @author Kalimar
 */
public class ClientStateInterception extends ClientState {
  
  protected ClientStateInterception(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.INTERCEPTION;
  }
    
  public void enterState() {
    super.enterState();
    setSelectable(true);
    setClickable(true);
  }
  
  protected void clickOnPlayer(Player pPlayer) {
    if (isInterceptor(pPlayer)) {
      getClient().getCommunication().sendInterceptorChoice(pPlayer);
    }
  }
  
  protected boolean mouseOverPlayer(Player pPlayer) {
    super.mouseOverPlayer(pPlayer);
    if (isInterceptor(pPlayer)) {
      UtilCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
    } else {
      UtilCursor.setDefaultCursor(getClient().getUserInterface());
    }
    return true;
  }
  
  protected boolean mouseOverField(FieldCoordinate pCoordinate) {
    super.mouseOverField(pCoordinate);
    UtilCursor.setDefaultCursor(getClient().getUserInterface());
    return true;
  }

  private boolean isInterceptor(Player pPlayer) {
    boolean isInterceptor = false;
    Game game = getClient().getGame();
    Player[] interceptors = UtilPassing.findInterceptors(game, game.getThrower(), game.getPassCoordinate());
    for (int i = 0; i < interceptors.length; i++) {
      if (interceptors[i] == pPlayer) {
        isInterceptor = true;
        break;
      }
    }
    return isInterceptor;
  }
      
}
