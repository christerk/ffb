package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.QuickSnapLogicModule;

public class ClientStateQuickSnap extends AbstractClientStateSetup<QuickSnapLogicModule> {

	protected ClientStateQuickSnap(FantasyFootballClientAwt pClient) {
		super(pClient, new QuickSnapLogicModule(pClient));
	}

	@Override
	public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
		return logicModule.squareIsOnPitch(pCoordinate);
	}

	@Override
	public boolean isDragAllowed(FieldCoordinate pCoordinate) {
		ClientData clientData = getClient().getClientData();
		return logicModule.squareIsEmpty(pCoordinate)
			&& logicModule.squaresAreSameOrAdjacent(pCoordinate, clientData.getDragStartPosition());
	}

	@Override
	public boolean isDropAllowed(FieldCoordinate pCoordinate) {
		ClientData clientData = getClient().getClientData();
		return logicModule.squaresAreSameOrAdjacent(pCoordinate, clientData.getDragStartPosition());
	}
}
