package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.ModifierAggregator;
import com.fumbbl.ffb.modifiers.SpecialEffectArmourModifier;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.util.Scanner;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.ARMOUR_MODIFIER)
@RulesCollection(RulesCollection.Rules.COMMON)
public class ArmorModifierFactory implements INamedObjectFactory<ArmorModifier> {

	private ModifierAggregator modifierAggregator;

	private ArmorModifiers armorModifiers;

	public ArmorModifier forName(String name) {
		return Stream.concat(armorModifiers.allValues(), modifierAggregator.getArmourModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	public Set<ArmorModifier> findArmorModifiers(Game game, Player<?> attacker, Player<?> defender, boolean isStab,
	                                             boolean isFoul) {

		if (UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.ignoresArmourModifiersFromSkills)) {
			return new HashSet<>(defender.getSkillWithProperty(NamedProperties.ignoresArmourModifiersFromSkills).getArmorModifiers());
		}

		ArmorModifierContext context = new ArmorModifierContext(game, attacker, defender, isStab, isFoul);
		Set<ArmorModifier> armorModifiers = getArmorModifiers(attacker, context);

		armorModifiers.stream().filter(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.reducesArmourToFixedValue)).findFirst()
			.ifPresent(armorModifier -> defender.getSkillsIncludingTemporaryOnes().stream().filter(skill -> armorModifier.getRegisteredTo() != null && skill.canCancel(armorModifier.getRegisteredTo())).findFirst()
				.ifPresent(cancelingSkill -> {
						armorModifiers.remove(armorModifier);
						armorModifiers.addAll(cancelingSkill.getArmorModifiers());
					}
				));

		return armorModifiers;
	}

	public Set<ArmorModifier> specialEffectArmourModifiers(SpecialEffect specialEffect, Player<?> defender) {
		if (UtilCards.hasUnusedSkillWithProperty(defender, NamedProperties.ignoresArmourModifiersFromSkills)) {
			return new HashSet<>(defender.getSkillWithProperty(NamedProperties.ignoresArmourModifiersFromSkills).getArmorModifiers());
		}

		return armorModifiers.values().filter(modifier -> modifier instanceof SpecialEffectArmourModifier)
			.map(modifier -> (SpecialEffectArmourModifier) modifier)
			.filter(modifier -> modifier.getEffect() == specialEffect)
			.collect(Collectors.toSet());
	}

	public Set<ArmorModifier> getFoulAssist(ArmorModifierContext context) {
		if (UtilCards.hasUnusedSkillWithProperty(context.getDefender(), NamedProperties.ignoresArmourModifiersFromSkills)) {
			return new HashSet<>(context.getDefender().getSkillWithProperty(NamedProperties.ignoresArmourModifiersFromSkills).getArmorModifiers());
		}

		return armorModifiers.values().filter(modifier -> !(modifier instanceof SpecialEffectArmourModifier) && modifier.appliesToContext(context)).collect(Collectors.toSet());
	}

	public ArmorModifier[] toArray(Set<ArmorModifier> pArmorModifiers) {
		if (pArmorModifiers != null) {
			ArmorModifier[] modifierArray = pArmorModifiers.toArray(new ArmorModifier[0]);
			Arrays.sort(modifierArray, Comparator.comparing(ArmorModifier::getName));
			return modifierArray;
		} else {
			return new ArmorModifier[0];
		}
	}


	@Override
	public void initialize(Game game) {
		modifierAggregator = game.getModifierAggregator();
		armorModifiers = new Scanner<>(ArmorModifiers.class).getInstancesImplementing(game.getOptions()).stream().findFirst().orElse(null);
		if (armorModifiers != null) {
			armorModifiers.setUseAll(((GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.BOMB_USES_MB)).isEnabled());
		}
	}

	private Set<ArmorModifier> getArmorModifiers(Player<?> player, ArmorModifierContext context) {
		return Arrays.stream(UtilCards.findAllSkills(player))
			.flatMap(skill -> skill.getArmorModifiers().stream())
			.filter(modifier -> modifier.appliesToContext(context))
			.collect(Collectors.toSet());
	}
}
