package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Finds the optional dodge modifiers a player could still use for a given dodge.
 * <p>
 * Optional modifiers are never applied automatically, they only apply when the coach selects the skill providing
 * them, so this service is the single place that knows which of them are currently available.
 */
public class OptionalDodgeModifierService {

	public List<OptionalDodgeModifier> availableFor(Game game, ActingPlayer actingPlayer, FieldCoordinate from,
																									FieldCoordinate to) {
		if (actingPlayer == null || actingPlayer.getPlayer() == null) {
			return Collections.emptyList();
		}

		List<OptionalDodgeModifier> availableModifiers = new ArrayList<>();

		for (Skill skill : actingPlayer.getPlayer().getSkillsIncludingTemporaryOnes()) {
			if (actingPlayer.isSkillUsed(skill)) {
				continue;
			}
			DodgeContext context = new DodgeContext(game, actingPlayer, from, to, Collections.singleton(skill));
			for (DodgeModifier modifier : skill.getDodgeModifiers()) {
				if (modifier.isOptional() && modifier.appliesToContext(skill, context)) {
					availableModifiers.add(new OptionalDodgeModifier(skill, modifier));
				}
			}
		}

		return availableModifiers;
	}
}
