package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.PickupModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.OptionalRollModifierService;
import com.fumbbl.ffb.modifiers.PickupContext;
import com.fumbbl.ffb.modifiers.PickupModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class PickupModifierSelectionService {
	private final AgilityModifierSelectionService selectionService = new AgilityModifierSelectionService();

	public List<ModifierChoiceOption> findOptions(Game game, Player<?> player, int roll) {
		return selectionService.findOptions(availableSkills(game, player), evaluator(game, player), roll);
	}

	public List<ModifierChoiceOption> findCombinations(Game game, Player<?> player) {
		return selectionService.findCombinations(availableSkills(game, player), evaluator(game, player));
	}

	private List<Skill> availableSkills(Game game, Player<?> player) {
		return new OptionalRollModifierService().availableSkills(game, player,
			skills -> new PickupContext(game, player, skills), Skill::getPickupModifiers);
	}

	private Function<Set<Skill>, ModifierChoiceOption> evaluator(Game game, Player<?> player) {
		return skills -> {
			PickupModifierFactory factory = game.getFactory(Factory.PICKUP_MODIFIER);
			Set<PickupModifier> modifiers = factory.findModifiers(new PickupContext(game, player, skills));
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC)
				.forName(Mechanic.Type.AGILITY.name());
			int bonus = modifiers.stream().filter(modifier -> modifier.isOptional()
				&& skills.stream().anyMatch(skill -> skill.getPickupModifiers().contains(modifier)))
				.mapToInt(PickupModifier::getModifier).sum();
			return new ModifierChoiceOption(new ArrayList<>(skills), bonus, mechanic.minimumRollPickup(player, modifiers));
		};
	}
}
