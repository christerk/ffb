package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.ui.SideBarComponent;
import com.balancedbytes.games.ffb.client.util.UtilCursor;

/**
 * 
 * @author Kalimar
 */
public class ClientStateKickoff extends ClientState {
  
  private boolean fKicked;
  
  protected ClientStateKickoff(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.KICKOFF;
  }
  
  public void enterState() {
    super.enterState();
    fKicked = false;
  }
  
  protected void clickOnField(FieldCoordinate pCoordinate) {
    if (!fKicked) {
      placeBall(pCoordinate);
    }
  }
  
  protected void clickOnPlayer(Player pPlayer) {
    if (!fKicked) {
      FieldCoordinate playerCoordinate = getClient().getGame().getFieldModel().getPlayerCoordinate(pPlayer);
      placeBall(playerCoordinate);
    }
  }
  
  private void placeBall(FieldCoordinate pCoordinate) {
    if ((pCoordinate != null) && FieldCoordinateBounds.HALF_AWAY.isInBounds(pCoordinate)) {
      getClient().getGame().getFieldModel().setBallMoving(true);
      getClient().getGame().getFieldModel().setBallCoordinate(pCoordinate);
      getClient().getUserInterface().getFieldComponent().refresh();
    }
  }
    
  @Override
  public void endTurn() {
    FieldCoordinate ballCoordinate = getClient().getGame().getFieldModel().getBallCoordinate(); 
    if ((ballCoordinate != null) && FieldCoordinateBounds.HALF_AWAY.isInBounds(ballCoordinate)) {
      fKicked = true;
      getClient().getCommunication().sendKickoff(ballCoordinate);
      getClient().getClientData().setEndTurnButtonHidden(true);
      SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
      sideBarHome.refresh();
      UtilCursor.setDefaultCursor(getClient().getUserInterface());
    }
  }
    
  protected boolean mouseOverField(FieldCoordinate pCoordinate) {
    super.mouseOverField(pCoordinate);
    if (!fKicked && (pCoordinate != null) && FieldCoordinateBounds.HALF_AWAY.isInBounds(pCoordinate)) {
      UtilCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
    } else {
      UtilCursor.setDefaultCursor(getClient().getUserInterface());
    }
    return true;
  }
   
}
