package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypeFireball extends InjuryTypeServer {
		public InjuryTypeFireball(IStep step) {
			super(step, "fireball", false, SendToBoxReason.FIREBALL);
		}


		@Override
		public InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

			if (!injuryContext.isArmorBroken()) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
				if (!injuryContext.isArmorBroken()) {
					injuryContext.addArmorModifier(ArmorModifier.MIGHTY_BLOW);
					injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
				}
			}

			if (injuryContext.isArmorBroken()) {
				injuryContext.setInjuryRoll(diceRoller.rollInjury());
				injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));

				if (!injuryContext.hasArmorModifier(ArmorModifier.MIGHTY_BLOW)) {
					injuryContext.addInjuryModifier(InjuryModifier.MIGHTY_BLOW);
				}

				setInjury(pDefender);
			} else {
				injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
			}

			return injuryContext;
		}
	}
