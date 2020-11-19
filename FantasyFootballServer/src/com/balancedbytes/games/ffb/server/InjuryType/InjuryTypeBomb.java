package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilCards;

public class InjuryTypeBomb extends InjuryTypeServer {
		public InjuryTypeBomb(IStep step) {
			super(step, "bomb", false, SendToBoxReason.BOMB);
		}

		@Override
		public InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

			if (!injuryContext.isArmorBroken()) {
				boolean defenderHasChainsaw = UtilCards.hasSkillWithProperty(pDefender,	NamedProperties.blocksLikeChainsaw);

				injuryContext.setArmorRoll(diceRoller.rollArmour());
				if (defenderHasChainsaw) {
					injuryContext.addArmorModifier(ArmorModifier.CHAINSAW);
				}
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}
			if (injuryContext.isArmorBroken()) {
				injuryContext.setInjuryRoll(diceRoller.rollInjury());
				injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));
				setInjury(pDefender);

			}
			return injuryContext;
		}
	}