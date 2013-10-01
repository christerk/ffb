package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.client.IClientPropertyValue;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class RangeGridHandler {

  private FantasyFootballClient fClient;
  private boolean fShowRangeGrid;
  private boolean fThrowTeamMate;

  public RangeGridHandler(FantasyFootballClient pClient, boolean pThrowTeamMate) {
    fClient = pClient;
    fThrowTeamMate = pThrowTeamMate;
  }
  
  public FantasyFootballClient getClient() {
    return fClient;
  }
    
  public void refreshRangeGrid() {
    boolean gridDrawn = false;
    UserInterface userInterface = getClient().getUserInterface();
    if (fShowRangeGrid) {
      Game game = getClient().getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      if ((!fThrowTeamMate && UtilPlayer.hasBall(game, actingPlayer.getPlayer()))
      	|| (fThrowTeamMate && (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE))
      	|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_BOMB)) {
        FieldCoordinate actingPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
        if (userInterface.getFieldComponent().getLayerRangeGrid().drawRangeGrid(actingPlayerCoordinate, fThrowTeamMate)) {
          userInterface.getFieldComponent().refresh();
        }
        gridDrawn = true;
      }
    }
    if (!gridDrawn && userInterface.getFieldComponent().getLayerRangeGrid().clearRangeGrid()) {
      userInterface.getFieldComponent().refresh();
    }
  }
      
  public void refreshSettings() {
    String rangeGridSettingProperty = getClient().getProperty(IClientProperty.SETTING_RANGEGRID);
    if (!fShowRangeGrid && IClientPropertyValue.SETTING_RANGEGRID_ALWAYS_ON.equals(rangeGridSettingProperty)) {
      setShowRangeGrid(true);
      refreshRangeGrid();
    }
  }

  public boolean isShowRangeGrid() {
    return fShowRangeGrid;
  }

  public void setShowRangeGrid(boolean pShowRangeGrid) {
    fShowRangeGrid = pShowRangeGrid;
  }
  
}
