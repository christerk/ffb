package com.fumbbl.ffb.server.InjuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.ArmorModifierFactory;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Optional;
import java.util.Set;

public class InjuryTypeFoul extends InjuryTypeServer<Foul> {
	public InjuryTypeFoul() {
		super(new Foul());
	}

	@Override
	public InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode) {

		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();

		// Blatant Foul breaks armor without roll
		if (game.isActive(NamedProperties.foulBreaksArmourWithoutRoll)) {
			injuryContext.setArmorBroken(true);
		}

		if (!injuryContext.isArmorBroken()) {

			Optional<Skill> attackerHasChainsaw = Optional.ofNullable(pAttacker.getSkillWithProperty(NamedProperties.blocksLikeChainsaw));

			injuryContext.setArmorRoll(diceRoller.rollArmour());
			attackerHasChainsaw.ifPresent(skill -> skill.getArmorModifiers().forEach(injuryContext::addArmorModifier));

			ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
			ArmorModifierContext context = new ArmorModifierContext(game, pAttacker, pDefender, false, true, UtilPlayer.findFoulAssists(game, pAttacker, pDefender));

			armorModifierFactory.getFoulAssist(context).forEach(injuryContext::addArmorModifier);

			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));

			if (!injuryContext.isArmorBroken()) {
				Set<ArmorModifier> armorModifiers = armorModifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(),
						isFoul());
				injuryContext.addArmorModifiers(armorModifiers);
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}

		}

		if (injuryContext.isArmorBroken()) {
			InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
			injuryContext.setInjuryRoll(diceRoller.rollInjury());
			injuryContext.addInjuryModifier(factory.getNigglingInjuryModifier(pDefender));

			Set<InjuryModifier> armorModifiers = factory.findInjuryModifiers(game, injuryContext, pAttacker,
					pDefender, isStab(), isFoul());
			injuryContext.addInjuryModifiers(armorModifiers);

			setInjury(pDefender, gameState, diceRoller);

		} else {
			injuryContext.setInjury(new PlayerState(PlayerState.PRONE));
		}

		return injuryContext;
	}
}
