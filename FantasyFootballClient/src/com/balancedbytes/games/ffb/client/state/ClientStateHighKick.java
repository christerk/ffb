package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.BoxType;
import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.ui.SideBarComponent;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public class ClientStateHighKick extends ClientState {
  
  private FieldCoordinate fOldCoordinate;
  
  protected ClientStateHighKick(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.HIGH_KICK;
  }
    
  public void enterState() {
    super.enterState();
    setSelectable(true);
    setClickable(true);
    fOldCoordinate = null;
  }
  
  protected void clickOnPlayer(Player pPlayer) {
    if (isPlayerSelectable(pPlayer)) {
      Game game = getClient().getGame();
      Player oldPlayer = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
      if (pPlayer != oldPlayer) {
        if ((oldPlayer != null) && (fOldCoordinate != null)) {
          getClient().getCommunication().sendSetupPlayer(oldPlayer, fOldCoordinate);
        }
        fOldCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
        getClient().getCommunication().sendSetupPlayer(pPlayer, game.getFieldModel().getBallCoordinate());
      }
    }
  }
  
  protected boolean mouseOverPlayer(Player pPlayer) {
    super.mouseOverPlayer(pPlayer);
    if (isPlayerSelectable(pPlayer)) {
      UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
    } else {
      UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
    }
    return true;
  }
  
  protected boolean mouseOverField(FieldCoordinate pCoordinate) {
    super.mouseOverField(pCoordinate);
    UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
    return true;
  }

  private boolean isPlayerSelectable(Player pPlayer) {
    boolean selectable = false;
    if (pPlayer != null) {
      Game game = getClient().getGame();
      PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
      selectable = ((playerState != null) && playerState.isActive() && game.getTeamHome().hasPlayer(pPlayer));
    }
    return selectable;
  }
 
  @Override
  public void endTurn() {
    SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
    if (sideBarHome.getOpenBox() == BoxType.RESERVES) {
      sideBarHome.closeBox();
    }
    getClient().getCommunication().sendEndTurn();
    getClient().getClientData().setEndTurnButtonHidden(true);
    sideBarHome.refresh();
  }
        
}
