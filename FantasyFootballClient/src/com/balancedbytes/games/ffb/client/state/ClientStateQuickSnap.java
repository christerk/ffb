package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.client.ClientData;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.Game;

public class ClientStateQuickSnap extends ClientStateSetup {

  protected ClientStateQuickSnap(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.QUICK_SNAP;
  }
  
  @Override
  public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
  	return ((pCoordinate != null) && !pCoordinate.isBoxCoordinate());
  }

  @Override
  public boolean isDragAllowed(FieldCoordinate pCoordinate) {
    Game game = getClient().getGame();
    ClientData clientData = getClient().getClientData();
    return ((pCoordinate != null) && (game.getFieldModel().getPlayer(pCoordinate) == null) && (pCoordinate.equals(clientData.getDragStartPosition()) || pCoordinate.isAdjacent(clientData.getDragStartPosition())));
  }

  @Override
  public boolean isDropAllowed(FieldCoordinate pCoordinate) {
    ClientData clientData = getClient().getClientData();
    return ((pCoordinate != null) && (pCoordinate.equals(clientData.getDragStartPosition()) || pCoordinate.isAdjacent(clientData.getDragStartPosition())));
  }

}
