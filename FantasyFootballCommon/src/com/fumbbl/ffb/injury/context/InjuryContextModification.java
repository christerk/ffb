package com.fumbbl.ffb.injury.context;

import com.fumbbl.ffb.model.skill.InjuryContextModificationSkill;

public abstract class InjuryContextModification {

	private InjuryContextModificationSkill skill;

	public void modifyArmour(InjuryContext injuryContext) {
		if (injuryContext.getAlternateInjuryContext() != null && !injuryContext.isArmorBroken()) {
			if (modifyArmourInternal(injuryContext)) {
				injuryContext.setSkillForAlternateContext(skill);
			}
		}
	}

	public void modifyInjury(InjuryContext injuryContext) {
		if (injuryContext.getAlternateInjuryContext() != null && !injuryContext.isCasualty()) {
			if (modifyInjuryInternal(injuryContext)) {
				injuryContext.setSkillForAlternateContext(skill);
			}
		}
	}

	abstract boolean modifyArmourInternal(InjuryContext injuryContext);

	abstract boolean modifyInjuryInternal(InjuryContext injuryContext);

	public InjuryContextModificationSkill getSkill() {
		return skill;
	}

	public void setSkill(InjuryContextModificationSkill skill) {
		this.skill = skill;
	}
}
