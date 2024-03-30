package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Game;

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
		return ((pCoordinate != null) && (game.getFieldModel().getPlayer(pCoordinate) == null)
				&& (pCoordinate.equals(clientData.getDragStartPosition())
			|| pCoordinate.isAdjacent(clientData.getDragStartPosition())));
	}

	@Override
	public boolean isDropAllowed(FieldCoordinate pCoordinate) {
		ClientData clientData = getClient().getClientData();
		return ((pCoordinate != null) && (pCoordinate.equals(clientData.getDragStartPosition())
			|| pCoordinate.isAdjacent(clientData.getDragStartPosition())));
	}

	@Override
	protected boolean useTurnMode() {
		return true;
	}
}
