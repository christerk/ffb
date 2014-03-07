package com.balancedbytes.games.ffb.client.state;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.balancedbytes.games.ffb.BoxType;
import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.ui.SideBarComponent;
import com.balancedbytes.games.ffb.client.util.UtilClientPlayerDrag;
import com.balancedbytes.games.ffb.dialog.DialogTeamSetupParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTeamSetupList;

/**
 * 
 * @author Kalimar
 */
public class ClientStateSetup extends ClientState {
  
  protected boolean fLoadDialog;
  private boolean fReservesBoxOpened;
  
  protected ClientStateSetup(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void enterState() {
    super.enterState();
    getClient().getClientData().clear();
    SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
    if (!sideBarHome.isBoxOpen()) {
      fReservesBoxOpened = true;
      sideBarHome.openBox(BoxType.RESERVES);
    }
  }
  
  public void leaveState() {
    SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
    if (fReservesBoxOpened && (sideBarHome.getOpenBox() == BoxType.RESERVES)) {
      sideBarHome.closeBox();
    }
  }
  
  public ClientStateId getId() {
    return ClientStateId.SETUP;
  }
  
  public void mouseEntered(MouseEvent pMouseEvent) {
  }
  
  public void mousePressed(MouseEvent pMouseEvent) {
    UtilClientPlayerDrag.mousePressed(getClient(), pMouseEvent, false);
  }
  
  public void mouseDragged(MouseEvent pMouseEvent) {
    UtilClientPlayerDrag.mouseDragged(getClient(), pMouseEvent, false);
  }

  public void mouseReleased(MouseEvent pMouseEvent) {
    if (SwingUtilities.isRightMouseButton(pMouseEvent)) {
      super.mouseReleased(pMouseEvent);
    } else {
      UtilClientPlayerDrag.mouseReleased(getClient(), pMouseEvent, false);
    }
  }
    
  public boolean actionKeyPressed(ActionKey pActionKey) {
    boolean actionHandled = true;
    switch (pActionKey) {
      case MENU_SETUP_LOAD:
        fLoadDialog = true;
        getClient().getCommunication().sendTeamSetupLoad(null);
        break;
      case MENU_SETUP_SAVE:
        fLoadDialog = false;
        getClient().getCommunication().sendTeamSetupLoad(null);
        break;
      default:
        actionHandled = false;
        break;
    }
    return actionHandled;
  }
  
  @Override
  public void endTurn() {
    SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
    if (sideBarHome.getOpenBox() == BoxType.RESERVES) {
      sideBarHome.closeBox();
    }
    UtilClientPlayerDrag.resetDragging(getClient());
    getClient().getCommunication().sendEndTurn();
  }
  
  public void handleCommand(NetCommand pNetCommand) {
    Game game = getClient().getGame();
    UserInterface userInterface = getClient().getUserInterface();
    switch (pNetCommand.getId()) {
      case SERVER_TEAM_SETUP_LIST:
        ServerCommandTeamSetupList setupListCommand = (ServerCommandTeamSetupList) pNetCommand;
        game.setDialogParameter(new DialogTeamSetupParameter(fLoadDialog, setupListCommand.getSetupNames()));
        userInterface.getDialogManager().updateDialog();
        break;
      default:
      	break;
    }
  }
  
  @Override
  public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
  	return true;
  }
    
  @Override
  public boolean isDragAllowed(FieldCoordinate pCoordinate) {
    Game game = getClient().getGame();
    return ((pCoordinate != null) && ((FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate) || pCoordinate.isBoxCoordinate()) && (game.getFieldModel().getPlayer(pCoordinate) == null)));
  }
  
  @Override
  public boolean isDropAllowed(FieldCoordinate pCoordinate) {
    return ((pCoordinate != null) && (FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate) || (pCoordinate.getX() == FieldCoordinate.RSV_HOME_X)));
  }
  
}
