package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.IInjuryContextModification;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.InjuryContextForModification;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;

import java.util.Set;

public abstract class InjuryContextModification implements IInjuryContextModification {

	private Skill skill;
	private final Set<Class<? extends InjuryType>> validInjuryTypes;

	public InjuryContextModification(Set<Class<? extends InjuryType>> validInjuryTypes) {
		this.validInjuryTypes = validInjuryTypes;
	}

	public boolean modifyArmour(InjuryContext injuryContext, GameState gameState) {
		if (injuryContext.getAlternateInjuryContext() == null && !injuryContext.isArmorBroken()) {
			InjuryContextForModification newContext = newContext(injuryContext);
			if (modifyArmourInternal(newContext, gameState)) {
				newContext.setSkillForAlternateContext(skill);
				injuryContext.setAlternateInjuryContext(newContext);
				return true;
			}
		}
		return false;
	}

	public boolean modifyInjury(InjuryContext injuryContext, GameState gameState) {
		if (injuryContext.getAlternateInjuryContext() == null && !injuryContext.isCasualty()) {
			InjuryContextForModification newContext = newContext(injuryContext);
			if (modifyInjuryInternal(newContext, gameState)) {
				newContext.setSkillForAlternateContext(skill);
				injuryContext.setAlternateInjuryContext(newContext);
				return true;
			}
		}
		return false;
	}

	protected abstract boolean modifyArmourInternal(InjuryContextForModification injuryContext, GameState gameState);

	protected abstract boolean modifyInjuryInternal(InjuryContextForModification injuryContext, GameState gameState);

	abstract SkillUse skillUse();

	public boolean isValidType(InjuryType injuryType) {
		return validInjuryTypes.contains(injuryType.getClass());
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	private InjuryContextForModification newContext(InjuryContext injuryContext) {
		InjuryContextForModification newContext = new InjuryContextForModification();
		newContext.fInjuryType = injuryContext.fInjuryType;
		newContext.fArmorModifiers = injuryContext.fArmorModifiers;
		newContext.fAttackerId = injuryContext.fAttackerId;
		newContext.fDefenderId = injuryContext.fDefenderId;
		newContext.fDefenderPosition = injuryContext.fDefenderPosition;
		newContext.fArmorBroken = injuryContext.fArmorBroken;
		newContext.fArmorRoll = new int[2];
		System.arraycopy(injuryContext.fArmorRoll, 0, newContext.fArmorRoll, 0, 2);
		newContext.fApothecaryMode = injuryContext.fApothecaryMode;
		newContext.fApothecaryStatus = injuryContext.fApothecaryStatus;
		newContext.setSkillUse(skillUse());
		return newContext;
	}
}
