package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.DodgeModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.OptionalRollModifierService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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

	private List<Skill> availableSkills(Game game, ActingPlayer actingPlayer, FieldCoordinate from, FieldCoordinate to) {
		return new OptionalRollModifierService().availableSkills(game, actingPlayer.getPlayer(),
			skills -> new DodgeContext(game, actingPlayer, from, to, skills), Skill::getDodgeModifiers);
	}

	private Function<Set<Skill>, ModifierChoiceOption> evaluator(Game game, ActingPlayer actingPlayer,
		FieldCoordinate from, FieldCoordinate to, Set<DodgeModifier> extraModifiers) {
		return skills -> {
			DodgeModifierFactory factory = game.getFactory(Factory.DODGE_MODIFIER);
			Set<DodgeModifier> modifiers = factory.findModifiers(new DodgeContext(game, actingPlayer, from, to, skills));
			if (extraModifiers != null) {
				modifiers.addAll(extraModifiers);
			}
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC)
				.forName(Mechanic.Type.AGILITY.name());
			int bonus = modifiers.stream().filter(modifier -> modifier.isOptional()
				&& skills.stream().anyMatch(skill -> skill.getDodgeModifiers().contains(modifier)))
				.mapToInt(DodgeModifier::getModifier).sum();
			return new ModifierChoiceOption(new ArrayList<>(skills), bonus,
				mechanic.minimumRollDodge(game, actingPlayer.getPlayer(), modifiers));
		};
	}

	public int usageCost(ModifierChoiceOption option) {
		return selectionService.usageCost(option);
	}

	public int usageCost(SkillUsageType usageType) {
		return selectionService.usageCost(usageType);
	}
}
