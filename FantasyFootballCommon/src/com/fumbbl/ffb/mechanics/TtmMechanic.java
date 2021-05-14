package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.PassModifier;

import java.util.Set;

public abstract class TtmMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.TTM;
	}

	public abstract Player<?>[] findThrowableTeamMates(Game pGame, Player<?> pThrower);

	public abstract boolean canBeThrown(Game game, Player<?> player);

	public abstract int minimumRoll(PassingDistance distance, Set<PassModifier> modifiers);

	public abstract int modifierSum(PassingDistance distance, Set<PassModifier> modifiers);

	public abstract boolean isValidEndScatterCoordinate(Game game, FieldCoordinate coordinate);
}
