package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.JumpModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.util.UtilCards;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Finds the optional modifier combinations that could rescue a failed jump.
 * <p>
 * {@link JumpContext} is mutable and Leap depends on the sum of the other modifiers, so every combination is
 * evaluated with a freshly built context. For the same reason the availability of a skill cannot be answered by the
 * modifier alone, it is answered by checking whether adding the skill to the current selection lowers the roll the
 * player needs.
 */
public class JumpModifierSelectionService {

	private final AgilityModifierSelectionService selectionService = new AgilityModifierSelectionService();

	public List<ModifierChoiceOption> findOptions(Game game, ActingPlayer actingPlayer, FieldCoordinate from,
		FieldCoordinate to, Set<Skill> selectedSkills, Set<JumpModifier> extraModifiers, boolean freeModifiersOptional,
		int roll) {
		Function<Set<Skill>, ModifierChoiceOption> evaluator =
			evaluator(game, actingPlayer, from, to, selectedSkills, extraModifiers, freeModifiersOptional);
		return selectionService.findOptions(
			availableSkills(game, actingPlayer, from, to, selectedSkills, extraModifiers, freeModifiersOptional),
			evaluator, roll);
	}

	public List<ModifierChoiceOption> findCombinations(Game game, ActingPlayer actingPlayer, FieldCoordinate from,
		FieldCoordinate to, Set<Skill> selectedSkills, Set<JumpModifier> extraModifiers, boolean freeModifiersOptional) {
		Function<Set<Skill>, ModifierChoiceOption> evaluator =
			evaluator(game, actingPlayer, from, to, selectedSkills, extraModifiers, freeModifiersOptional);
		return selectionService.findCombinations(
			availableSkills(game, actingPlayer, from, to, selectedSkills, extraModifiers, freeModifiersOptional),
			evaluator);
	}

	/**
	 * @return the unused skills that lower the needed roll when they are added to the current selection
	 */
	private List<Skill> availableSkills(Game game, ActingPlayer actingPlayer, FieldCoordinate from, FieldCoordinate to,
		Set<Skill> selectedSkills, Set<JumpModifier> extraModifiers, boolean freeModifiersOptional) {
		List<Skill> available = new ArrayList<>();
		if (actingPlayer.getPlayer() == null) {
			return available;
		}

		int minimumRoll = minimumRoll(game, actingPlayer, from, to, selectedSkills, extraModifiers, freeModifiersOptional);

		for (Skill skill : UtilCards.findAllSkills(actingPlayer.getPlayer())) {
			if (actingPlayer.isSkillUsed(skill) || !isCandidate(skill, freeModifiersOptional)) {
				continue;
			}
			Set<Skill> candidate = new LinkedHashSet<>(selectedSkills);
			candidate.add(skill);
			if (minimumRoll(game, actingPlayer, from, to, candidate, extraModifiers, freeModifiersOptional) < minimumRoll) {
				available.add(skill);
			}
		}
		return available;
	}

	/**
	 * @return whether the skill may be selected at all, i.e. it either registers an optional jump modifier or it is a
	 * skill ignoring the jump modifiers that the coach is allowed to decline
	 */
	private boolean isCandidate(Skill skill, boolean freeModifiersOptional) {
		if (skill.getJumpModifiers().stream().anyMatch(JumpModifier::isOptional)) {
			return true;
		}
		return freeModifiersOptional && skill.hasSkillProperty(NamedProperties.ignoreTacklezonesWhenJumping);
	}

	private int minimumRoll(Game game, ActingPlayer actingPlayer, FieldCoordinate from, FieldCoordinate to,
		Set<Skill> skills, Set<JumpModifier> extraModifiers, boolean freeModifiersOptional) {
		JumpModifierFactory factory = game.getFactory(Factory.JUMP_MODIFIER);
		JumpContext context =
			new JumpContext(game, actingPlayer.getPlayer(), from, to, skills, freeModifiersOptional);
		if (extraModifiers != null) {
			// the extra modifiers are part of the roll, so they have to be accumulated before Leap is evaluated
			extraModifiers.forEach(modifier -> context.addModifierValue(modifier.getModifier()));
		}
		Set<JumpModifier> modifiers = factory.findModifiers(context);
		if (extraModifiers != null) {
			modifiers.addAll(extraModifiers);
		}
		return mechanic(game).minimumRollJump(actingPlayer.getPlayer(), modifiers);
	}

	private Function<Set<Skill>, ModifierChoiceOption> evaluator(Game game, ActingPlayer actingPlayer,
		FieldCoordinate from, FieldCoordinate to, Set<Skill> selectedSkills, Set<JumpModifier> extraModifiers,
		boolean freeModifiersOptional) {
		int baseMinimumRoll =
			minimumRoll(game, actingPlayer, from, to, selectedSkills, extraModifiers, freeModifiersOptional);
		return skills -> {
			Set<Skill> combination = new LinkedHashSet<>(selectedSkills);
			combination.addAll(skills);
			int minimumRoll =
				minimumRoll(game, actingPlayer, from, to, combination, extraModifiers, freeModifiersOptional);
			// the bonus of a combination cannot be read off the modifiers alone, declining a skill may add modifiers
			return new ModifierChoiceOption(new ArrayList<>(skills), minimumRoll - baseMinimumRoll, minimumRoll);
		};
	}

	private AgilityMechanic mechanic(Game game) {
		return game.getMechanic(Mechanic.Type.AGILITY);
	}
}
