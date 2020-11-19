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
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.server.model.ServerSkill;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilCards;

public class InjuryTypePilingOnInjury extends InjuryTypeServer {
		public InjuryTypePilingOnInjury(IStep step) {
			super(step, "pilingOnInjury", true, SendToBoxReason.PILED_ON);
		}

		@Override
		public boolean isCausedByOpponent() {
			return true;
		}


		@Override
		public InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

			if (pOldInjuryContext != null) {
				for (ArmorModifier armorModifier : pOldInjuryContext.getArmorModifiers()) {
					injuryContext.addArmorModifier(armorModifier);
				}
			}

			if (!injuryContext.isArmorBroken()) {
				injuryContext.setArmorBroken(true);
			}

			if (injuryContext.isArmorBroken()) {
				injuryContext.setInjuryRoll(diceRoller.rollInjury());
				injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));

				if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK)) {
					if (UtilCards.hasSkill(game, pAttacker, ServerSkill.MIGHTY_BLOW)
							&& !injuryContext.hasArmorModifier(ArmorModifier.MIGHTY_BLOW)) {
						injuryContext.addInjuryModifier(InjuryModifier.MIGHTY_BLOW);
					}
				}

				setInjury(pDefender);
			} else {
				injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
			}

			return injuryContext;
		}
	}