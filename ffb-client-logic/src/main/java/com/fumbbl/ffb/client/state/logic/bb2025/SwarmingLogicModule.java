package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.SetupLogicModule;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;

import java.util.Optional;

public class SwarmingLogicModule extends SetupLogicModule {

	public SwarmingLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.SWARMING;
	}

	public boolean squareHasSwarmingPlayer(FieldCoordinate pCoordinate) {
		Optional<Player<?>> player = getPlayer(pCoordinate);
		return player.isPresent() && player.get().getPosition().getKeywords().contains(Keyword.LINEMAN);
	}

	public boolean squareIsValidForSwarming(FieldCoordinate pCoordinate) {
		return (pCoordinate != null) && ((FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate) || pCoordinate.isBoxCoordinate())
				&& !getPlayer(pCoordinate).isPresent());
	}
}
