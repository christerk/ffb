package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;

public abstract class GameMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.GAME;
	}

	public abstract void updateTurnDataAfterReRollUsage(TurnData turnData);

	public abstract int minimumLonerRoll(Player<?> player);

	public abstract int minimumProRoll();

	public abstract boolean eligibleForPro(ActingPlayer actingPlayer, Player<?> player);

}
