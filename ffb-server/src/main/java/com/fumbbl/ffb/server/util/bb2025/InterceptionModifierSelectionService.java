package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.InterceptionModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.InterceptionContext;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.OptionalRollModifierService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class InterceptionModifierSelectionService {
	private final AgilityModifierSelectionService selectionService = new AgilityModifierSelectionService();

	public List<ModifierChoiceOption> findOptions(Game game, Player<?> player, PassResult passResult, boolean bomb,
																								int roll) {
		return selectionService.findOptions(availableSkills(game, player, passResult, bomb),
			evaluator(game, player, passResult, bomb), roll);
	}

	public List<ModifierChoiceOption> findCombinations(Game game, Player<?> player, PassResult passResult,
																										 boolean bomb) {
		return selectionService.findCombinations(availableSkills(game, player, passResult, bomb),
			evaluator(game, player, passResult, bomb));
	}

	private List<Skill> availableSkills(Game game, Player<?> player, PassResult passResult, boolean bomb) {
		return new OptionalRollModifierService().availableSkills(game, player,
			skills -> new InterceptionContext(game, player, passResult, bomb, skills), Skill::getInterceptionModifiers);
	}

	private Function<Set<Skill>, ModifierChoiceOption> evaluator(Game game, Player<?> player, PassResult passResult,
																															boolean bomb) {
		return skills -> {
			InterceptionModifierFactory factory = game.getFactory(Factory.INTERCEPTION_MODIFIER);
			Set<InterceptionModifier> modifiers =
				factory.findModifiers(new InterceptionContext(game, player, passResult, bomb, skills));
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC)
				.forName(Mechanic.Type.AGILITY.name());
			int bonus = modifiers.stream().filter(modifier -> modifier.isOptional()
					&& skills.stream().anyMatch(skill -> skill.getInterceptionModifiers().contains(modifier)))
				.mapToInt(InterceptionModifier::getModifier).sum();
			return new ModifierChoiceOption(new ArrayList<>(skills), bonus,
				mechanic.minimumRollInterception(player, modifiers));
		};
	}
}
