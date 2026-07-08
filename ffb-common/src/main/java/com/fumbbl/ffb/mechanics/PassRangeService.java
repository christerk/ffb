package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 * Shared service that determines whether a pass target is within the thrower's
 * passing range. The same check is required on the client (to guard the user
 * interaction) and on the server (to guard against manipulated third party
 * clients), so the logic lives here in the commons module.
 */
public class PassRangeService {

	/**
	 * Determines whether the given target coordinate is a valid, in-range pass
	 * target for the given thrower. Hail Mary throws are not limited by the
	 * passing range and are considered in range as long as a target is provided.
	 */
	public boolean isInRange(Game game, Player<?> thrower, FieldCoordinate targetCoordinate, PlayerAction throwerAction) {
		if ((PlayerAction.HAIL_MARY_PASS == throwerAction) || (PlayerAction.HAIL_MARY_BOMB == throwerAction)) {
			return targetCoordinate != null;
		}
		return isInRange(game, thrower, targetCoordinate);
	}

	/**
	 * Determines whether the given target coordinate is within the thrower's
	 * passing range, ignoring any Hail Mary handling.
	 */
	public boolean isInRange(Game game, Player<?> thrower, FieldCoordinate targetCoordinate) {
		if ((game == null) || (thrower == null) || (targetCoordinate == null)) {
			return false;
		}
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(thrower);
		if (throwerCoordinate == null) {
			return false;
		}
		PassMechanic mechanic = game.getMechanic(Mechanic.Type.PASS);
		return mechanic.findPassingDistance(game, throwerCoordinate, targetCoordinate, false) != null;
	}
}
