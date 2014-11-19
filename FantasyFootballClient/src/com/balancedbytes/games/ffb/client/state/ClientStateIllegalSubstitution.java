package com.balancedbytes.games.ffb.client.state;

import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.client.ClientData;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public class ClientStateIllegalSubstitution extends ClientStateSetup {
  
  private Set<Player> fFieldPlayers;

  protected ClientStateIllegalSubstitution(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.ILLEGAL_SUBSTITUTION;
  }
  
  @Override
  public void enterState() {
    super.enterState();
    Game game = getClient().getGame();
    fFieldPlayers = new HashSet<Player>();
    for (Player player : game.getTeamHome().getPlayers()) {
      if (!game.getFieldModel().getPlayerCoordinate(player).isBoxCoordinate()) {
        fFieldPlayers.add(player);
      }
    }
  }
  
  @Override
  public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
    if (pCoordinate != null) {
      Game game = getClient().getGame();
      Player draggedPlayer = game.getFieldModel().getPlayer(pCoordinate);
      if (draggedPlayer != null) {
        if (pCoordinate.isBoxCoordinate()) {
          for (FieldCoordinate coordinate : FieldCoordinateBounds.ENDZONE_HOME.fieldCoordinates()) {
            Player player = game.getFieldModel().getPlayer(coordinate);
            if ((player != null) && game.getTeamHome().hasPlayer(player) && !fFieldPlayers.contains(player)) {
              return false;
            }
          }
          return true;
        } else {
          return !fFieldPlayers.contains(draggedPlayer);
        }
      }
    }
    return false;
  }

  @Override
  public boolean isDragAllowed(FieldCoordinate pCoordinate) {
    if (pCoordinate == null) {
      return false;
    }
    Game game = getClient().getGame();
    ClientData clientData = getClient().getClientData();
    return ((clientData.getDragStartPosition() != null) && (game.getFieldModel().getPlayer(pCoordinate) == null) && (pCoordinate.isBoxCoordinate() || FieldCoordinateBounds.ENDZONE_HOME.isInBounds(pCoordinate)));
  }

  @Override
  public boolean isDropAllowed(FieldCoordinate pCoordinate) {
    if (pCoordinate == null) {
      return false;
    }
    return (pCoordinate.isBoxCoordinate() || FieldCoordinateBounds.ENDZONE_HOME.isInBounds(pCoordinate));
  }

}
