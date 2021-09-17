package com.fumbbl.ffb.server.mechanic;

import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceRoller;

public abstract class RollMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.ROLL;
	}

	public abstract int[] rollCasualty(DiceRoller diceRoller);

	public abstract PlayerState interpretInjuryRoll(Game game, InjuryContext pInjuryContext);

	public abstract PlayerState interpretCasualtyRollAndAddModifiers(Game game, InjuryContext injuryContext, Player<?> player, boolean useDecayRoll);

	public abstract SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext);
	public abstract SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, boolean useDecay);

	public abstract SeriousInjury interpretSeriousInjuryRoll(Game game, InjuryContext injuryContext, int[] roll);

	public abstract int multiBlockAttackerModifier();

	public abstract int multiBlockDefenderModifier();
}
