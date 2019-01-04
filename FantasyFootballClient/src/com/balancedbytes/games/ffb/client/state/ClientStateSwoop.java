package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.FieldComponent;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.layer.FieldLayerRangeRuler;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;
import com.balancedbytes.games.ffb.util.UtilRangeRuler;

/**
 * 
 * @author Kalimar
 */
public class ClientStateSwoop extends ClientStateMove {
  
  protected ClientStateSwoop(FantasyFootballClient pClient) {
    super(pClient);
  }

  public ClientStateId getId() {
    return ClientStateId.SWOOP;
  }
  
  protected void clickOnField(FieldCoordinate pCoordinate) {
    ActingPlayer actingPlayer = getClient().getGame().getActingPlayer();
    UserInterface userInterface = getClient().getUserInterface();
    if (actingPlayer.getPlayerAction() == PlayerAction.SWOOP) {
      userInterface.getFieldComponent().refresh();
      getClient().getCommunication().sendSwoop(actingPlayer.getPlayerId(), pCoordinate);
    }    
  }
  
  protected boolean mouseOverPlayer(Player pPlayer) {
    Game game = getClient().getGame();
    UserInterface userInterface = getClient().getUserInterface();
    if ((game.getDefender() == null) && (game.getPassCoordinate() == null)) {
      UtilClientCursor.setDefaultCursor(userInterface);
    }
//    if ((PlayerAction.THROW_TEAM_MATE == actingPlayer.getPlayerAction()) && (game.getPassCoordinate() == null)) {
    getClient().getClientData().setSelectedPlayer(pPlayer);
    userInterface.refreshSideBars();
    return true;
  }
  
  @Override
  public void leaveState() {
    // clear marked players
    UserInterface userInterface = getClient().getUserInterface();
    userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
    userInterface.getFieldComponent().refresh();
  }
}
