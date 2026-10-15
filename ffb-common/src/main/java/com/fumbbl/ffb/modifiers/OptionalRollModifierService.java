package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class OptionalRollModifierService {

	public <C extends ModifierContext, M extends RollModifier<C>> List<Skill> availableSkills(
		Game game, Player<?> player, Function<Set<Skill>, C> contextFactory,
		Function<Skill, ? extends Collection<M>> modifiersForSkill) {
		List<Skill> available = new ArrayList<>();
		if (player == null) {
			return available;
		}
		for (Skill skill : player.getSkillsIncludingTemporaryOnes()) {
			boolean used = player == game.getActingPlayer().getPlayer()
				? game.getActingPlayer().isSkillUsed(skill) : player.isUsed(skill);
			if (!used && modifiersForSkill.apply(skill).stream().anyMatch(modifier ->
				modifier.isOptional() && modifier.appliesToContext(skill, contextFactory.apply(Collections.singleton(skill))))) {
				available.add(skill);
			}
		}
		return available;
	}
}
