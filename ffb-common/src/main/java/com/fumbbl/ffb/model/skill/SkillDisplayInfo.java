package com.fumbbl.ffb.model.skill;

public class SkillDisplayInfo {
	private final String info;
	private final Category category;
	private final Skill skill;

	public SkillDisplayInfo(String info, Category category, Skill skill) {
		this.info = info;
		this.category = category;
		this.skill = skill;
	}

	public String getInfo() {
		return info;
	}

	public Category getCategory() {
		return category;
	}

	public Skill getSkill() {
		return skill;
	}

	public enum Category {
		ROSTER, PLAYER, TEMPORARY
	}
}
