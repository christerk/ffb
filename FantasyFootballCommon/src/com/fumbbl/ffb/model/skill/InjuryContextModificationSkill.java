package com.fumbbl.ffb.model.skill;

import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.context.InjuryContextModification;

public abstract class InjuryContextModificationSkill extends Skill {
	private final InjuryContextModification modification;
	private final SkillUse skillUse;

	public InjuryContextModificationSkill(String name, SkillCategory category, SkillUsageType skillUsageType,
	                                      InjuryContextModification modification, SkillUse skillUse) {
		super(name, category, skillUsageType);
		assert skillUse != null;
		this.skillUse = skillUse;
		assert modification != null;
		this.modification = modification;
		modification.setSkill(this);
	}

	public InjuryContextModification getModification() {
		return modification;
	}

	public SkillUse getSkillUse() {
		return skillUse;
	}
}
