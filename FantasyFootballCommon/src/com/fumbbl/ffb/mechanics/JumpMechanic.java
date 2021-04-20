package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public abstract class JumpMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.JUMP;
	}

	public abstract boolean isAvailableAsNextMove(Game game, ActingPlayer player, boolean jumping);

	public abstract boolean canStillJump(Game game, ActingPlayer actingPlayer);

	public abstract boolean canJump(Game game, Player<?> player, FieldCoordinate coordinate);

	public abstract boolean isValidJump(Game game, Player<?> player, FieldCoordinate from, FieldCoordinate to);
}
