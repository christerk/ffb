package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.PilingOnKnockedOut;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

public class InjuryTypePilingOnKnockedOut extends InjuryTypeServer<PilingOnKnockedOut> {
	public InjuryTypePilingOnKnockedOut(IStep step) {
		super(new PilingOnKnockedOut());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		injuryContext.setArmorBroken(true);

		injuryContext.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));

		return injuryContext;
	}
}