package com.fumbbl.ffb.model.skill;

import java.util.Optional;

public class SkillWithValue {
	private final Skill skill;
	private String value;

	public SkillWithValue(Skill skill) {
		this.skill = skill;
	}

	public SkillWithValue(Skill skill, String value) {
		this.skill = skill;
		this.value = value;
	}

	public Skill getSkill() {
		return skill;
	}

	public Optional<String> getValue() {
		return Optional.ofNullable(value);
	}
}
