package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.ArmorModifierFactory;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.StabForSpp;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Optional;
import java.util.Set;

public class InjuryTypeStabForSpp extends ModificationAwareInjuryTypeServer<StabForSpp> {
	private final boolean useInjuryModifiers;
	private final boolean addDefenderChainsaw;

	public InjuryTypeStabForSpp(boolean useInjuryModifiers) {
		this(useInjuryModifiers, false);
	}

	public InjuryTypeStabForSpp(boolean useInjuryModifiers, boolean addDefenderChainsaw) {
		super(new StabForSpp());
		this.useInjuryModifiers = useInjuryModifiers;
		this.addDefenderChainsaw = addDefenderChainsaw;
		super.setFailedArmourPlacesProne(false);
	}

	@Override
	protected void injuryRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, InjuryContext injuryContext) {
		injuryContext.setInjuryRoll(diceRoller.rollInjury());

		if (useInjuryModifiers) {
			InjuryModifierFactory factory = game.getFactory(FactoryType.Factory.INJURY_MODIFIER);
			Set<InjuryModifier> injuryModifiers = factory.findInjuryModifiers(game, injuryContext, pAttacker,
				pDefender, isStab(), isFoul(), isVomitLike());
			injuryContext.addInjuryModifiers(injuryModifiers);
		}
		setInjury(pDefender, gameState, diceRoller);
	}

	@Override
	protected void armourRoll(Game game, GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender, DiceInterpreter diceInterpreter, InjuryContext injuryContext, boolean roll) {
		if (!injuryContext.isArmorBroken()) {

			ArmorModifierFactory armorModifierFactory = game.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
			Set<ArmorModifier> modifiers = armorModifierFactory.findArmorModifiers(game, pAttacker, pDefender, isStab(), isFoul());
			
			Optional<ArmorModifier> sneakyPair = modifiers.stream()
				.filter(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryWithPartner))
				.findFirst();
			sneakyPair.ifPresent(modifiers::remove);

			injuryContext.addArmorModifiers(modifiers);

			if (UtilCards.hasUnusedSkillWithProperty(pDefender, NamedProperties.ignoresArmourModifiersFromSkills)) {
				injuryContext.addArmorModifiers(pDefender.getSkillWithProperty(NamedProperties.ignoresArmourModifiersFromSkills).getArmorModifiers());
			} else {
				Skill chainsaw = addDefenderChainsaw ? pDefender.getSkillWithProperty(NamedProperties.blocksLikeChainsaw) : null;
				if (chainsaw != null) {
					injuryContext.addArmorModifiers(chainsaw.getArmorModifiers());
				}
			}

			if (roll) {
				injuryContext.setArmorRoll(diceRoller.rollArmour());
			}
			injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));

			if (!injuryContext.isArmorBroken() && sneakyPair.isPresent()) {
				injuryContext.addArmorModifier(sneakyPair.get());
				injuryContext.setArmorBroken(diceInterpreter.isArmourBroken(gameState, injuryContext));
			}
		}
	}

	@Override
	protected void savedByArmour(InjuryContext injuryContext) {
		injuryContext.setInjury(null);
	}
}