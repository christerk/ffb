package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypeBallAndChain extends InjuryTypeServer {
		public InjuryTypeBallAndChain(IStep step) {
			super(step, "ballAndChain", false, SendToBoxReason.BALL_AND_CHAIN);
		}


		@Override
		public InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {
			// ball and chain always breaks armor on being knocked down
			injuryContext.setArmorBroken(true);

			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));
			setInjury(pDefender);

			return injuryContext;
		}

	}