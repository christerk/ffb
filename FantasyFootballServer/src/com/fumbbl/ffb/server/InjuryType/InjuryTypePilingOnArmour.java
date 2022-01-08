package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.ArmorModifierFactory;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.PilingOnArmour;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

import java.util.Optional;
import java.util.Set;

public class InjuryTypePilingOnArmour extends InjuryTypeServer<PilingOnArmour> {
	public InjuryTypePilingOnArmour() {
		super(new PilingOnArmour());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                                  Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                                  ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		if (!injuryContext.isArmorBroken()) {
			injuryContext.setArmorRoll(diceRoller.rollArmour());
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK) && !injuryContext.isArmorBroken()) {
				ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
				Set<ArmorModifier> armorModifiers = armorModifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(),
					isFoul());
				Optional<ArmorModifier> claw = armorModifiers.stream()
					.filter(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.reducesArmourToFixedValue)).findFirst();
				if (claw.isPresent()) {
					injuryContext.addArmorModifier(claw.get());
					injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
					if (!injuryContext.isArmorBroken()) {
						if (UtilGameOption.isOptionEnabled(game, GameOptionId.CLAW_DOES_NOT_STACK)) {
							armorModifiers.remove(claw.get());
							injuryContext.clearArmorModifiers();
							injuryContext.addArmorModifiers(armorModifiers);
							injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
							if (!injuryContext.isArmorBroken()) {
								// set claw as modifier as it should be displayed as used in the log when there is no stacking to avoid confusion
								injuryContext.clearArmorModifiers();
								injuryContext.addArmorModifier(claw.get());
							}
						} else {
							injuryContext.addArmorModifiers(armorModifiers);
						}
						injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
					}
				} else {
					injuryContext.addArmorModifiers(armorModifiers);
					injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));

				}
			}
		}

		if (injuryContext.isArmorBroken()) {
			InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			factory.getNigglingInjuryModifier(pDefender).ifPresent(injuryContext::addInjuryModifier);

			if (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_DOES_NOT_STACK)) {
				Set<InjuryModifier> armorModifiers = factory.findInjuryModifiersWithoutNiggling(game, injuryContext, pAttacker,
					pDefender, isStab(), isFoul(), isVomit());
				injuryContext.addInjuryModifiers(armorModifiers);
			}
			setInjury(pDefender, gameState, diceRoller);
		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}

		return injuryContext;
	}
}