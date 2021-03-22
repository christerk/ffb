package com.balancedbytes.games.ffb.mechanics;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public abstract class JumpMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.JUMP;
	}

	public abstract boolean isAvailableAsNextMove(Game game, ActingPlayer player, boolean jumping);

	public abstract boolean canStillJump(Game game, ActingPlayer actingPlayer);

	public abstract boolean canJump(Game game, Player<?> player, FieldCoordinate coordinate);

	public abstract boolean isValidJump(Game game, FieldCoordinate from, FieldCoordinate to);
}
