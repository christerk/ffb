package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypeBlockStunned extends InjuryTypeServer {
		public InjuryTypeBlockStunned(IStep step) {
			super(step, "blockStunned", false, SendToBoxReason.BLOCKED);
		}

		@Override
		public boolean isCausedByOpponent() {
			return true;
		}

		@Override
		public InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

			if (!injuryContext.isArmorBroken()) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}
			if (injuryContext.isArmorBroken()) {
				injuryContext.setInjuryRoll(diceRoller.rollInjury());
				injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));
				setInjury(pDefender);
			} else {
				injuryContext.setInjury(new PlayerState(PlayerState.STUNNED));
			}

			return injuryContext;
		}
	}