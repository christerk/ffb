package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.DodgeModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.OptionalRollModifierService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Finds the optional modifier combinations that could rescue a failed dodge.
 * <p>
 * A skill ignoring every dodge modifier once the die is known (e.g. Swift As The Breeze) is offered like an optional
 * modifier. With such a skill selected the dodge only needs the unmodified roll, all other selected skills and every
 * other modifier, including Diving Tackle, no longer matter.
 */
public class DodgeModifierSelectionService {
	private final AgilityModifierSelectionService selectionService = new AgilityModifierSelectionService();

	public List<ModifierChoiceOption> findOptions(Game game, ActingPlayer actingPlayer, FieldCoordinate from,
		FieldCoordinate to, Set<DodgeModifier> extraModifiers, int roll) {
		return selectionService.findOptions(availableSkills(game, actingPlayer, from, to),
			evaluator(game, actingPlayer, from, to, extraModifiers), roll);
	}

	public List<ModifierChoiceOption> findCombinations(Game game, ActingPlayer actingPlayer, FieldCoordinate from,
		FieldCoordinate to, Set<DodgeModifier> extraModifiers) {
		return selectionService.findCombinations(availableSkills(game, actingPlayer, from, to),
			evaluator(game, actingPlayer, from, to, extraModifiers));
	}

	/**
	 * @return the optional modifier skills the player can still use for the given dodge, including an unused skill
	 * ignoring every dodge modifier after the roll
	 */
	public List<Skill> availableSkills(Game game, ActingPlayer actingPlayer, FieldCoordinate from, FieldCoordinate to) {
		List<Skill> available = new OptionalRollModifierService().availableSkills(game, actingPlayer.getPlayer(),
			skills -> new DodgeContext(game, actingPlayer, from, to, skills), Skill::getDodgeModifiers);
		if (actingPlayer.getPlayer() != null) {
			for (Skill skill : actingPlayer.getPlayer().getSkillsIncludingTemporaryOnes()) {
				if (isModifierIgnoringSkill(skill) && !actingPlayer.isSkillUsed(skill) && !available.contains(skill)) {
					available.add(skill);
				}
			}
		}
		return available;
	}

	/**
	 * @return whether the skill lets the player ignore every dodge modifier once the die is known, e.g. Swift As The
	 * Breeze
	 */
	public boolean isModifierIgnoringSkill(Skill skill) {
		return skill != null && skill.hasSkillProperty(NamedProperties.canChooseToIgnoreDodgeModifierAfterRoll);
	}

	/**
	 * @return the modifiers applying to the dodge with the given skills selected, none at all when one of them ignores
	 * every dodge modifier
	 */
	public Set<DodgeModifier> modifiers(Game game, ActingPlayer actingPlayer, FieldCoordinate from, FieldCoordinate to,
		Set<Skill> skills, Set<DodgeModifier> extraModifiers) {
		if (skills.stream().anyMatch(this::isModifierIgnoringSkill)) {
			return new HashSet<>();
		}
		DodgeModifierFactory factory = game.getFactory(Factory.DODGE_MODIFIER);
		Set<DodgeModifier> modifiers = factory.findModifiers(new DodgeContext(game, actingPlayer, from, to, skills));
		if (extraModifiers != null) {
			modifiers.addAll(extraModifiers);
		}
		return modifiers;
	}

	/**
	 * @return the roll the player needs for the dodge with the given skills selected
	 */
	public int minimumRoll(Game game, ActingPlayer actingPlayer, FieldCoordinate from, FieldCoordinate to,
		Set<Skill> skills, Set<DodgeModifier> extraModifiers) {
		return mechanic(game).minimumRollDodge(game, actingPlayer.getPlayer(),
			modifiers(game, actingPlayer, from, to, skills, extraModifiers));
	}

	private Function<Set<Skill>, ModifierChoiceOption> evaluator(Game game, ActingPlayer actingPlayer,
		FieldCoordinate from, FieldCoordinate to, Set<DodgeModifier> extraModifiers) {
		return skills -> {
			int minimumRoll = minimumRoll(game, actingPlayer, from, to, skills, extraModifiers);
			int bonus;
			if (skills.stream().anyMatch(this::isModifierIgnoringSkill)) {
				// ignoring the modifiers is no modifier itself, so its bonus is what it changes about the needed roll
				bonus = minimumRoll - minimumRoll(game, actingPlayer, from, to, Collections.emptySet(), extraModifiers);
			} else {
				bonus = modifiers(game, actingPlayer, from, to, skills, extraModifiers).stream()
					.filter(modifier -> modifier.isOptional()
						&& skills.stream().anyMatch(skill -> skill.getDodgeModifiers().contains(modifier)))
					.mapToInt(DodgeModifier::getModifier).sum();
			}
			return new ModifierChoiceOption(new ArrayList<>(skills), bonus, minimumRoll);
		};
	}

	private AgilityMechanic mechanic(Game game) {
		return (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
	}
}
