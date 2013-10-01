package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.BoxType;
import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.ui.SideBarComponent;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class ClientStateWaitForSetup extends ClientState {
  
  private boolean fReservesBoxOpened;
    
  protected ClientStateWaitForSetup(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.WAIT_FOR_SETUP;
  }
    
  public void enterState() {
    super.enterState();
    setSelectable(true);
    setClickable(false);
    Game game = getClient().getGame();
    SideBarComponent sideBarAway = getClient().getUserInterface().getSideBarAway();
    fReservesBoxOpened = ((game.getTurnMode() == TurnMode.SETUP) && !sideBarAway.isBoxOpen());
    if (fReservesBoxOpened) {
      sideBarAway.openBox(BoxType.RESERVES);
    }
  }
  
  public void leaveState() {
    SideBarComponent sideBarAway = getClient().getUserInterface().getSideBarAway();
    if (fReservesBoxOpened && (sideBarAway.getOpenBox() == BoxType.RESERVES)) {
      sideBarAway.closeBox();
    }
  }
      
}
