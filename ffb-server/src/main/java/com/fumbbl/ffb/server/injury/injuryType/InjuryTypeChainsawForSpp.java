package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.injury.ChainsawForSpp;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;

public class InjuryTypeChainsawForSpp extends ModificationAwareInjuryTypeServer<ChainsawForSpp> {
	public InjuryTypeChainsawForSpp() {
		super(new ChainsawForSpp());
		super.setFailedArmourPlacesProne(false);
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, InjuryContext injuryContext) {
		injuryContext.setInjuryRoll(diceRoller.rollInjury());
		InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		factory.findInjuryModifiers(game, injuryContext, pAttacker,
			pDefender, isStab(), isFoul(), isVomitLike()).forEach(injuryContext::addInjuryModifier);

		setInjury(pDefender, gameState, diceRoller, injuryContext);
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {
		if (!injuryContext.isArmorBroken()) {
			if (roll) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
			}
			if (UtilCards.hasUnusedSkillWithProperty(pDefender, NamedProperties.ignoresArmourModifiersFromSkills)) {
				injuryContext.addArmorModifiers(pDefender.getSkillWithProperty(NamedProperties.ignoresArmourModifiersFromSkills).getArmorModifiers());
			} else {
				if (Arrays.stream(injuryContext.getArmorModifiers())
					.noneMatch(armorModifier -> armorModifier instanceof StaticArmourModifier
						&& ((StaticArmourModifier) armorModifier).isChainsaw())) {
					SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
					factory.getSkills().stream()
						.filter(skill -> skill.hasSkillProperty(NamedProperties.blocksLikeChainsaw))
						.flatMap(skill -> skill.getArmorModifiers().stream())
						.forEach(injuryContext::addArmorModifier);
				}
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
		}

	}

	@Override
	protected void savedByArmour(InjuryContext injuryContext) {
		injuryContext.setInjury(null);
	}
}