package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.server.DiceInterpreter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enumerates and ranks optional modifier combinations independently of the agility test.
 */
public class AgilityModifierSelectionService {

	// keeps the subset enumeration (2^n) safe, no player is expected to have more optional modifiers than this
	private static final int MAX_OPTIONAL_MODIFIERS = 5;

	/**
	 * @return the combinations that would turn the given roll into a success, without the ones that are not minimal
	 */
	public List<ModifierChoiceOption> findOptions(List<Skill> skills,
		Function<Set<Skill>, ModifierChoiceOption> evaluator, int roll) {

		if (roll <= 0) {
			return new ArrayList<>();
		}

		List<ModifierChoiceOption> options = new ArrayList<>();
		List<Set<Skill>> successfulCombinations = new ArrayList<>();

		// evaluated combinations are ordered by ascending size, so a minimal combination is always seen first
		for (ModifierChoiceOption combination : evaluateCombinations(skills, evaluator)) {
			if (!DiceInterpreter.getInstance().isSkillRollSuccessful(roll, combination.getMinimumRoll())) {
				continue;
			}
			Set<Skill> selected = new LinkedHashSet<>(combination.getSkills());
			if (successfulCombinations.stream().anyMatch(selected::containsAll)) {
				// a smaller combination already succeeds, this one is not minimal
				continue;
			}
			successfulCombinations.add(selected);
			options.add(combination);
		}

		options.sort(Comparator.comparingInt((ModifierChoiceOption option) -> option.getSkills().size())
			.thenComparingInt(this::usageCost).thenComparing(ModifierChoiceOption::getLabel));

		return options;
	}

	/**
	 * @return every combination of the available optional modifier skills with the roll it would require, so that the
	 * coach can judge what a re-roll could achieve
	 */
	public List<ModifierChoiceOption> findCombinations(List<Skill> skills,
		Function<Set<Skill>, ModifierChoiceOption> evaluator) {

		List<ModifierChoiceOption> combinations =
			removeRedundantSupersets(evaluateCombinations(skills, evaluator));

		combinations.sort(Comparator.comparingInt(ModifierChoiceOption::getMinimumRoll)
			.thenComparingInt((ModifierChoiceOption option) -> option.getSkills().size())
			.thenComparingInt(this::usageCost).thenComparing(ModifierChoiceOption::getLabel));

		return combinations;
	}

	/**
	 * @return one entry per non empty combination of the available optional modifier skills, ordered by ascending
	 * combination size
	 */
	private List<ModifierChoiceOption> evaluateCombinations(List<Skill> skills,
		Function<Set<Skill>, ModifierChoiceOption> evaluator) {

		if (skills.isEmpty()) {
			return new ArrayList<>();
		}

		if (skills.size() > MAX_OPTIONAL_MODIFIERS) {
			skills = new ArrayList<>(skills.subList(0, MAX_OPTIONAL_MODIFIERS));
		}

		List<ModifierChoiceOption> combinations = new ArrayList<>();

		for (Set<Skill> combination : combinations(skills)) {
			combinations.add(evaluator.apply(combination));
		}

		return removeRedundantSingleSkills(combinations);
	}

	/**
	 * Drops combinations that need the same roll as a combination they contain, as the additional skills would not
	 * improve anything. The given combinations have to be ordered by ascending size, so a contained combination is
	 * always seen first.
	 */
	private List<ModifierChoiceOption> removeRedundantSupersets(List<ModifierChoiceOption> combinations) {
		List<ModifierChoiceOption> kept = new ArrayList<>();

		for (ModifierChoiceOption combination : combinations) {
			Set<Skill> skills = new LinkedHashSet<>(combination.getSkills());
			boolean redundant = kept.stream().anyMatch(other -> other.getMinimumRoll() == combination.getMinimumRoll()
				&& other.getSkills().size() < skills.size() && skills.containsAll(other.getSkills()));
			if (!redundant) {
				kept.add(combination);
			}
		}

		return kept;
	}

	/**
	 * Drops single skills that give the same bonus as another single skill that can be used more often, as using the
	 * scarcer skill would only waste it. Combinations of several skills are kept, they still add up to a larger bonus.
	 */
	private List<ModifierChoiceOption> removeRedundantSingleSkills(List<ModifierChoiceOption> combinations) {
		Map<Integer, Integer> cheapestCostPerBonus = new HashMap<>();
		combinations.stream().filter(this::isSingleSkill).forEach(option -> cheapestCostPerBonus
			.merge(option.getTotalModifier(), usageCost(option), Math::min));

		return combinations.stream()
			.filter(option -> !isSingleSkill(option)
				|| usageCost(option) <= cheapestCostPerBonus.getOrDefault(option.getTotalModifier(), 0))
			.collect(Collectors.toList());
	}

	private boolean isSingleSkill(ModifierChoiceOption option) {
		return option.getSkills().size() == 1;
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
