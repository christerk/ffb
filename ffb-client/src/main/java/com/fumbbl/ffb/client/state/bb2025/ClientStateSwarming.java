package com.fumbbl.ffb.client.state.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateSetup;
import com.fumbbl.ffb.client.state.logic.bb2025.SwarmingLogicModule;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ClientStateSwarming extends AbstractClientStateSetup<SwarmingLogicModule> {

	public ClientStateSwarming(FantasyFootballClientAwt pClient) {
		super(pClient, new SwarmingLogicModule(pClient));
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
