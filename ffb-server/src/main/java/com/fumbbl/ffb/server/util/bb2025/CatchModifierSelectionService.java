package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.CatchModifierFactory;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.CatchContext;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.modifiers.OptionalRollModifierService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CatchModifierSelectionService {
	private final AgilityModifierSelectionService selectionService = new AgilityModifierSelectionService();

	public List<ModifierChoiceOption> findOptions(Game game, Player<?> player, CatchScatterThrowInMode catchMode,
	                                              Boolean usingBlastIt, int roll) {
		return selectionService.findOptions(availableSkills(game, player, catchMode, usingBlastIt),
			evaluator(game, player, catchMode, usingBlastIt), roll);
	}

	public List<ModifierChoiceOption> findCombinations(Game game, Player<?> player, CatchScatterThrowInMode catchMode,
	                                                   Boolean usingBlastIt) {
		return selectionService.findCombinations(availableSkills(game, player, catchMode, usingBlastIt),
			evaluator(game, player, catchMode, usingBlastIt));
	}

	private List<Skill> availableSkills(Game game, Player<?> player, CatchScatterThrowInMode catchMode,
	                                    Boolean usingBlastIt) {
		return new OptionalRollModifierService().availableSkills(game, player,
			skills -> new CatchContext(game, player, catchMode, usingBlastIt, skills), Skill::getCatchModifiers);
	}

	private Function<Set<Skill>, ModifierChoiceOption> evaluator(Game game, Player<?> player,
	                                                            CatchScatterThrowInMode catchMode, Boolean usingBlastIt) {
		return skills -> {
			CatchModifierFactory factory = game.getFactory(Factory.CATCH_MODIFIER);
			Set<CatchModifier> modifiers =
				factory.findModifiers(new CatchContext(game, player, catchMode, usingBlastIt, skills));
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC)
				.forName(Mechanic.Type.AGILITY.name());
			int bonus = modifiers.stream().filter(modifier -> modifier.isOptional()
					&& skills.stream().anyMatch(skill -> skill.getCatchModifiers().contains(modifier)))
				.mapToInt(CatchModifier::getModifier).sum();
			return new ModifierChoiceOption(new ArrayList<>(skills), bonus, mechanic.minimumRollCatch(player, modifiers));
		};
	}
}
