package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.INamedObjectFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.ARMOUR_MODIFIER)
@RulesCollection(RulesCollection.Rules.COMMON)
public class ArmorModifierFactory implements INamedObjectFactory {

	private ModifierAggregator modifierAggregator;

	private final Set<ArmorModifier> foulAssists = new HashSet<ArmorModifier>() {{
		add(new ArmorModifier("1 Offensive Assist", 1, true));
		add(new ArmorModifier("2 Offensive Assists", 2, true));
		add(new ArmorModifier("3 Offensive Assists", 3, true));
		add(new ArmorModifier("4 Offensive Assists", 4, true));
		add(new ArmorModifier("5 Offensive Assists", 5, true));
		add(new ArmorModifier("6 Offensive Assists", 6, true));
		add(new ArmorModifier("7 Offensive Assists", 7, true));
		add(new ArmorModifier("1 Defensive Assist", -1, true));
		add(new ArmorModifier("2 Defensive Assists", -2, true));
		add(new ArmorModifier("3 Defensive Assists", -3, true));
		add(new ArmorModifier("4 Defensive Assists", -4, true));
		add(new ArmorModifier("5 Defensive Assists", -5, true));
		add(new ArmorModifier("Foul", 1, false));
	}};

	public ArmorModifier forName(String name) {
		return Stream.concat(foulAssists.stream(), modifierAggregator.getArmourModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	public Set<ArmorModifier> findArmorModifiers(Game game, Player<?> attacker, Player<?> defender, boolean isStab,
			boolean isFoul) {

		ArmorModifierContext context = new ArmorModifierContext(game, attacker, defender, isStab, isFoul);
		Map<ArmorModifier, Skill> armorModifiers = getArmorModifiers(attacker, context);

		if (UtilGameOption.isOptionEnabled(game, GameOptionId.CLAW_DOES_NOT_STACK)
			&& armorModifiers.values().stream().anyMatch(skill ->
			skill.hasSkillProperty(NamedProperties.reducesArmourToFixedValue))
		) {
			Optional<Skill> suppressedSkill = armorModifiers.values().stream().filter(skill ->
				skill.hasSkillProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock)).findFirst();
			if (suppressedSkill.isPresent()) {
				return armorModifiers.entrySet().stream().filter(entry -> entry.getValue() != suppressedSkill.get()).map(Map.Entry::getKey).collect(Collectors.toSet());
			}
		}

		return armorModifiers.keySet();
	}

	public ArmorModifier getFoulAssist(int pModifier) {
		for (ArmorModifier modifier : foulAssists) {
			if (modifier.isFoulAssistModifier() && (modifier.getModifier() == pModifier)) {
				return modifier;
			}
		}
		return null;
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
	}

	private Map<ArmorModifier, Skill> getArmorModifiers(Player<?> player, ArmorModifierContext context) {
		Map<ArmorModifier, Skill> result = new HashMap<>();
		for (Skill skill : player.getSkills()) {
			for (ArmorModifier modifier : skill.getArmorModifiers()) {
				if (modifier.appliesToContext(context)) {
					result.put(modifier, skill);
				}
			}
		}
		return result;
	}
}
