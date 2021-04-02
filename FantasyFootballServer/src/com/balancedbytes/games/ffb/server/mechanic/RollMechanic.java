package com.balancedbytes.games.ffb.server.mechanic;

import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.DiceRoller;

public abstract class RollMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.ROLL;
	}

	public abstract int[] rollCasualty(DiceRoller diceRoller);

	public abstract PlayerState interpretInjuryRoll(Game game, InjuryContext pInjuryContext);

	public abstract PlayerState interpretCasualtyRoll(int[] roll, Player<?> player);

	public abstract SeriousInjury interpretSeriousInjuryRoll(int[] roll);
}
