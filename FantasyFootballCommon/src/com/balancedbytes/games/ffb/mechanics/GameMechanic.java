package com.balancedbytes.games.ffb.mechanics;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.TurnData;

public abstract class GameMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.GAME;
	}

	public abstract void updateTurnDataAfterReRollUsage(TurnData turnData);

	public abstract int minimumLonerRoll(Player<?> player);

	public abstract int minimumProRoll();
}
