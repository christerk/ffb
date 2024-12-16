package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;

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
		return player.isPresent() && player.get().hasSkillProperty(NamedProperties.canSneakExtraPlayersOntoPitch);
	}

	public boolean squareIsValidForSwarming(FieldCoordinate pCoordinate) {
		return (pCoordinate != null) && ((FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate)
			&& !FieldCoordinateBounds.LOS_HOME.isInBounds(pCoordinate)
			&& !FieldCoordinateBounds.LOWER_WIDE_ZONE_HOME.isInBounds(pCoordinate)
			&& !FieldCoordinateBounds.UPPER_WIDE_ZONE_HOME.isInBounds(pCoordinate) || pCoordinate.isBoxCoordinate())
			&& getPlayer(pCoordinate).isPresent());
	}
}
