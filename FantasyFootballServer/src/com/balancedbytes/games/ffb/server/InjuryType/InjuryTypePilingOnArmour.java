package com.balancedbytes.games.ffb.server.InjuryType;

import java.util.Set;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.ArmorModifierFactory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.injury.PilingOnArmour;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;

public class InjuryTypePilingOnArmour extends InjuryTypeServer<PilingOnArmour>  {
	public InjuryTypePilingOnArmour() {
		super(new PilingOnArmour());
	}

	
	@Override
	public InjuryContext handleInjury(IStep step, Game game,GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
			FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode) {
		
		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK)) {
				ArmorModifierFactory modifierFactory = new ArmorModifierFactory();
				Set<ArmorModifier> armorModifiers = modifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(), isFoul());
				injuryContext.addArmorModifiers(armorModifiers);
			}
		}

		if (injuryContext.isArmorBroken()) {
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(new InjuryModifierFactory().getNigglingInjuryModifier(pDefender));

			if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK)) {
				InjuryModifierFactory modifierFactory = new InjuryModifierFactory();
				Set<InjuryModifier> armorModifiers = modifierFactory.findInjuryModifiers(game, injuryContext, pAttacker, pDefender, isStab(), isFoul());
				injuryContext.addInjuryModifiers(armorModifiers);
			}
			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}

		return injuryContext;
	}
}