package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ArmorModifiers;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.injury.Bomb;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilCards;

public class InjuryTypeBomb extends InjuryTypeServer<Bomb>  {
		public InjuryTypeBomb() {
			super(new Bomb());
		}

		@Override
		public InjuryContext handleInjury(IStep step, Game game,GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

			DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

			if (!injuryContext.isArmorBroken()) {
				boolean defenderHasChainsaw = UtilCards.hasSkillWithProperty(pDefender,	NamedProperties.blocksLikeChainsaw);

				injuryContext.setArmorRoll(diceRoller.rollArmour());
				if (defenderHasChainsaw) {
					injuryContext.addArmorModifier(ArmorModifiers.CHAINSAW);
				}
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}
			if (injuryContext.isArmorBroken()) {
				injuryContext.setInjuryRoll(diceRoller.rollInjury());
				injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));
				setInjury(pDefender, gameState, diceRoller);

			}
			return injuryContext;
		}
	}