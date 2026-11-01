package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.JumpUpModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpUpContext;
import com.fumbbl.ffb.modifiers.JumpUpModifier;
import com.fumbbl.ffb.modifiers.OptionalRollModifierService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class JumpUpModifierSelectionService {
	private final AgilityModifierSelectionService selectionService = new AgilityModifierSelectionService();

	public List<ModifierChoiceOption> findOptions(Game game, int roll) {
		return selectionService.findOptions(availableSkills(game), evaluator(game), roll);
	}

	public List<ModifierChoiceOption> findCombinations(Game game) {
		return selectionService.findCombinations(availableSkills(game), evaluator(game));
	}

	private List<Skill> availableSkills(Game game) {
		return new OptionalRollModifierService().availableSkills(game, game.getActingPlayer().getPlayer(),
			skills -> new JumpUpContext(game.getActingPlayer(), game, skills), Skill::getJumpUpModifiers);
	}

	private Function<Set<Skill>, ModifierChoiceOption> evaluator(Game game) {
		return skills -> {
			JumpUpModifierFactory factory = game.getFactory(Factory.JUMP_UP_MODIFIER);
			Set<JumpUpModifier> modifiers = factory.findModifiers(new JumpUpContext(game.getActingPlayer(), game, skills));
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC)
				.forName(Mechanic.Type.AGILITY.name());
			int bonus = modifiers.stream().filter(modifier -> modifier.isOptional()
				&& skills.stream().anyMatch(skill -> skill.getJumpUpModifiers().contains(modifier)))
				.mapToInt(JumpUpModifier::getModifier).sum();
			return new ModifierChoiceOption(new ArrayList<>(skills), bonus,
				mechanic.minimumRollJumpUp(game.getActingPlayer().getPlayer(), modifiers));
		};
	}
}
