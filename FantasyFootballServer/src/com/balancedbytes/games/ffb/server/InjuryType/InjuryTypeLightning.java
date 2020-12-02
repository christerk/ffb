package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ArmorModifiers;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.InjuryModifiers;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.injury.Lightning;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypeLightning extends InjuryTypeServer<Lightning>   {
		public InjuryTypeLightning() {
			super(new Lightning());
		}


		@Override
		public InjuryContext handleInjury(IStep step, Game game,GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
				FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {

			DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

			if (!injuryContext.isArmorBroken()) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
				if (!injuryContext.isArmorBroken()) {
					injuryContext.addArmorModifier(ArmorModifiers.MIGHTY_BLOW);
					injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
				}
			}

			if (injuryContext.isArmorBroken()) {
				injuryContext.setInjuryRoll(diceRoller.rollInjury());
				injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));

				if (!injuryContext.hasArmorModifier(ArmorModifiers.MIGHTY_BLOW)) {
					injuryContext.addInjuryModifier(InjuryModifiers.MIGHTY_BLOW);
				}

				setInjury(pDefender, gameState, diceRoller);
			} else {
				injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
			}

			return injuryContext;
		}
	}
