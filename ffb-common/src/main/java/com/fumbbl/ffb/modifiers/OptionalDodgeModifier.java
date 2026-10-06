package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.skill.Skill;

/**
 * Pairing of a skill with one of its optional dodge modifiers.
 */
public class OptionalDodgeModifier {

	private final Skill skill;
	private final DodgeModifier modifier;

	public OptionalDodgeModifier(Skill skill, DodgeModifier modifier) {
		this.skill = skill;
		this.modifier = modifier;
	}

	public Skill getSkill() {
		return skill;
	}

	public DodgeModifier getModifier() {
		return modifier;
	}
}
