package com.fumbbl.ffb.injury.context;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.model.skill.InjuryContextModificationSkill;

import java.util.Set;

public abstract class InjuryContextModification {

	private InjuryContextModificationSkill skill;
	private final Set<InjuryType> validInjuryTypes;

	public InjuryContextModification(Set<InjuryType> validInjuryTypes) {
		this.validInjuryTypes = validInjuryTypes;
	}

	public void modifyArmour(InjuryContext injuryContext) {
		if (injuryContext.getAlternateInjuryContext() != null && !injuryContext.isArmorBroken()) {
			if (modifyArmourInternal(injuryContext)) {
				injuryContext.getAlternateInjuryContext().setSkillForAlternateContext(skill);
			}
		}
	}

	public void modifyInjury(InjuryContext injuryContext) {
		if (injuryContext.getAlternateInjuryContext() != null && !injuryContext.isCasualty()) {
			if (modifyInjuryInternal(injuryContext)) {
				injuryContext.getAlternateInjuryContext().setSkillForAlternateContext(skill);
			}
		}
	}

	abstract boolean modifyArmourInternal(InjuryContext injuryContext);

	abstract boolean modifyInjuryInternal(InjuryContext injuryContext);

	public boolean isValidType(InjuryType injuryType) {
		return validInjuryTypes.contains(injuryType);
	}

	public InjuryContextModificationSkill getSkill() {
		return skill;
	}

	public void setSkill(InjuryContextModificationSkill skill) {
		this.skill = skill;
	}
}
