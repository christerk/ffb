package com.fumbbl.ffb.server.injury.injuryType.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.injury.bb2025.KTMFumbleInjury;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeServer;
import com.fumbbl.ffb.server.step.IStep;

public class InjuryTypeFumbledKtm extends InjuryTypeServer<KTMFumbleInjury> {

	public InjuryTypeFumbledKtm() {
		super(new KTMFumbleInjury());
	}

	@Override
	public void handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                         Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                         ApothecaryMode pApothecaryMode) {

		injuryContext.setArmorBroken(true);
		injuryContext.setInjuryRoll(diceRoller.rollInjury());

		setInjury(pDefender, gameState, diceRoller);

	}

	@Override
	public boolean stunIsTreatedAsKo() {
		return true;
	}
}