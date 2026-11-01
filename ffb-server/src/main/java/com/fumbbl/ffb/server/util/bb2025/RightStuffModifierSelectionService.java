package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.RightStuffModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.OptionalRollModifierService;
import com.fumbbl.ffb.modifiers.RightStuffContext;
import com.fumbbl.ffb.modifiers.RightStuffModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class RightStuffModifierSelectionService {
	private final AgilityModifierSelectionService selectionService = new AgilityModifierSelectionService();

	public List<ModifierChoiceOption> findOptions(Game game, Player<?> player, PassResult passResult, int roll) {
		return selectionService.findOptions(availableSkills(game, player, passResult), evaluator(game, player, passResult),
			roll);
	}

	public List<ModifierChoiceOption> findCombinations(Game game, Player<?> player, PassResult passResult) {
		return selectionService.findCombinations(availableSkills(game, player, passResult),
			evaluator(game, player, passResult));
	}

	private List<Skill> availableSkills(Game game, Player<?> player, PassResult passResult) {
		return new OptionalRollModifierService().availableSkills(game, player,
			skills -> new RightStuffContext(game, player, passResult, skills), Skill::getRightStuffModifiers);
	}

	private Function<Set<Skill>, ModifierChoiceOption> evaluator(Game game, Player<?> player, PassResult passResult) {
		return skills -> {
			RightStuffModifierFactory factory = game.getFactory(Factory.RIGHT_STUFF_MODIFIER);
			Set<RightStuffModifier> modifiers =
				factory.findModifiers(new RightStuffContext(game, player, passResult, skills));
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC)
				.forName(Mechanic.Type.AGILITY.name());
			int bonus = modifiers.stream().filter(modifier -> modifier.isOptional()
					&& skills.stream().anyMatch(skill -> skill.getRightStuffModifiers().contains(modifier)))
				.mapToInt(RightStuffModifier::getModifier).sum();
			return new ModifierChoiceOption(new ArrayList<>(skills), bonus,
				mechanic.minimumRollRightStuff(player, modifiers));
		};
	}
}
