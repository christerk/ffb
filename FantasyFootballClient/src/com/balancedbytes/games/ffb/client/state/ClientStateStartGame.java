package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.BoxType;
import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;

/**
 * 
 * @author Kalimar
 */
public class ClientStateStartGame extends ClientState {
  
  protected ClientStateStartGame(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.START_GAME;
  }
  
  public void enterState() {
    super.enterState();
    setSelectable(true);
    setClickable(false);
    UserInterface userInterface = getClient().getUserInterface();
    userInterface.getSideBarAway().openBox(BoxType.RESERVES);
  }
    
  public void leaveState() {
    closeAwayBox();
  }
  
  private void closeAwayBox() {
    UserInterface userInterface = getClient().getUserInterface();
    if (BoxType.RESERVES == userInterface.getSideBarAway().getBoxComponent().getOpenBox()) {
      userInterface.getSideBarAway().closeBox();
    }
  }
  
}
