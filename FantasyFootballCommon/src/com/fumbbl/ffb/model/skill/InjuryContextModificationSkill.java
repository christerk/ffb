package com.fumbbl.ffb.model.skill;

import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.injury.context.InjuryContextModification;

public abstract class InjuryContextModificationSkill extends Skill {
	private final InjuryContextModification modification;

	public InjuryContextModificationSkill(String name, SkillCategory category, SkillUsageType skillUsageType, InjuryContextModification modification) {
		super(name, category, skillUsageType);

		this.modification = modification;
	}

	public InjuryContextModification getModification() {
		return modification;
	}
}
