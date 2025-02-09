package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;

public class QuickSnapLogicModule extends SetupLogicModule {

	public QuickSnapLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.QUICK_SNAP;
	}

	public boolean squareIsOnPitch(FieldCoordinate pCoordinate) {
		return pCoordinate != null && !pCoordinate.isBoxCoordinate();
	}

	public boolean squaresAreSameOrAdjacent(FieldCoordinate start, FieldCoordinate end) {
		return start != null && end != null && (start.equals(end) || start.isAdjacent(end));
	}

	@Override
	protected boolean useTurnMode() {
		return true;
	}
}
