package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.SwarmingLogicModule;

public class ClientStateSwarming extends AbstractClientStateSetup<SwarmingLogicModule> {

	public ClientStateSwarming(FantasyFootballClientAwt pClient) {
		super(pClient, new SwarmingLogicModule(pClient));
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.SWARMING;
	}

	@Override
	public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			return logicModule.squareHasSwarmingPlayer(pCoordinate);
		}

		return false;
	}

	@Override
	public boolean isDragAllowed(FieldCoordinate pCoordinate) {
		return logicModule.squareIsValidForSwarming(pCoordinate);

	}

}
