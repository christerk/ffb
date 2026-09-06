package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.DodgeModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.OptionalDodgeModifier;
import com.fumbbl.ffb.modifiers.OptionalDodgeModifierService;
import com.fumbbl.ffb.server.DiceInterpreter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Finds the combinations of optional dodge modifier skills that would turn a given dodge roll into a success.
 */
public class DodgeModifierSelectionService {

	// keeps the subset enumeration (2^n) safe, no player is expected to have more optional modifiers than this
	private static final int MAX_OPTIONAL_MODIFIERS = 5;

	private final OptionalDodgeModifierService optionalModifierService = new OptionalDodgeModifierService();

	public List<ModifierChoiceOption> findOptions(Game game, ActingPlayer actingPlayer, FieldCoordinate from,
																							 FieldCoordinate to, Set<DodgeModifier> extraModifiers, int dodgeRoll) {

		List<Skill> skills = optionalModifierService.availableFor(game, actingPlayer, from, to).stream()
			.map(OptionalDodgeModifier::getSkill).distinct().collect(Collectors.toList());

		if (skills.isEmpty() || dodgeRoll <= 0) {
			return new ArrayList<>();
		}

		if (skills.size() > MAX_OPTIONAL_MODIFIERS) {
			skills = new ArrayList<>(skills.subList(0, MAX_OPTIONAL_MODIFIERS));
		}

		AgilityMechanic mechanic =
			(AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
		DodgeModifierFactory modifierFactory = game.getFactory(Factory.DODGE_MODIFIER);

		List<ModifierChoiceOption> options = new ArrayList<>();
		List<Set<Skill>> successfulCombinations = new ArrayList<>();

		for (Set<Skill> combination : combinations(skills)) {
			Set<DodgeModifier> modifiers =
				modifierFactory.findModifiers(new DodgeContext(game, actingPlayer, from, to, combination));
			if (extraModifiers != null) {
				modifiers.addAll(extraModifiers);
			}
			int minimumRoll = mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), modifiers);
			if (!DiceInterpreter.getInstance().isSkillRollSuccessful(dodgeRoll, minimumRoll)) {
				continue;
			}
			if (successfulCombinations.stream().anyMatch(combination::containsAll)) {
				// a smaller combination already succeeds, this one is not minimal
				continue;
			}
			successfulCombinations.add(combination);
			options.add(new ModifierChoiceOption(orderedSkills(skills, combination), totalModifier(combination, modifiers),
				minimumRoll));
		}

		options.sort(Comparator.comparingInt((ModifierChoiceOption option) -> option.getSkills().size())
			.thenComparingInt(this::usageCost).thenComparing(ModifierChoiceOption::getLabel));

		return options;
	}

	public int usageCost(ModifierChoiceOption option) {
		return option.getSkills().stream().mapToInt(skill -> usageCost(skill.getSkillUsageType())).sum();
	}

	public int usageCost(SkillUsageType usageType) {
		if (usageType == null) {
			return 0;
		}
		switch (usageType) {
			case ONCE_PER_DRIVE:
				return 1;
			case ONCE_PER_HALF:
				return 2;
			case ONCE_PER_GAME:
			case SPECIAL:
				return 3;
			default:
				return 0;
		}
	}

	private List<Skill> orderedSkills(List<Skill> skills, Set<Skill> combination) {
		return skills.stream().filter(combination::contains).collect(Collectors.toList());
	}

	private int totalModifier(Set<Skill> combination, Set<DodgeModifier> modifiers) {
		return modifiers.stream().filter(modifier -> modifier.isOptional()
				&& combination.stream().anyMatch(skill -> skill.getDodgeModifiers().contains(modifier)))
			.mapToInt(DodgeModifier::getModifier).sum();
	}

	/**
	 * @return all non empty subsets of the given skills, ordered by ascending size
	 */
	private List<Set<Skill>> combinations(List<Skill> skills) {
		List<Set<Skill>> combinations = new ArrayList<>();
		for (int mask = 1; mask < (1 << skills.size()); mask++) {
			Set<Skill> combination = new LinkedHashSet<>();
			for (int index = 0; index < skills.size(); index++) {
				if ((mask & (1 << index)) != 0) {
					combination.add(skills.get(index));
				}
			}
			combinations.add(combination);
		}
		combinations.sort(Comparator.comparingInt(Set::size));
		return combinations;
	}
}
