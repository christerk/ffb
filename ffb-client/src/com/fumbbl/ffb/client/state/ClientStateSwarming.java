package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;

public class ClientStateSwarming extends ClientStateSetup {

	public ClientStateSwarming(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.SWARMING;
	}

	@Override
	public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
		if (pCoordinate != null) {
			Player<?> player = getClient().getGame().getFieldModel().getPlayer(pCoordinate);
			return player != null && player.hasSkillProperty(NamedProperties.canSneakExtraPlayersOntoPitch);
		}

		return false;
	}

	@Override
	public boolean isDragAllowed(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		return ((pCoordinate != null) && ((FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate)
				&& !FieldCoordinateBounds.LOS_HOME.isInBounds(pCoordinate)
				&& !FieldCoordinateBounds.LOWER_WIDE_ZONE_HOME.isInBounds(pCoordinate)
				&& !FieldCoordinateBounds.UPPER_WIDE_ZONE_HOME.isInBounds(pCoordinate) || pCoordinate.isBoxCoordinate())
				&& (game.getFieldModel().getPlayer(pCoordinate) == null)));

	}
}
