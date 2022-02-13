package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.IInjuryContextModification;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.InjuryModification;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;

import java.util.Set;

public abstract class InjuryContextModification<T extends ModificationParams> implements IInjuryContextModification {

	private Skill skill;
	private final Set<Class<? extends InjuryType>> validInjuryTypes;

	public InjuryContextModification(Set<Class<? extends InjuryType>> validInjuryTypes) {
		this.validInjuryTypes = validInjuryTypes;
	}

	protected abstract T params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType);

	public boolean modifyArmour(GameState gameState, InjuryContext injuryContext, InjuryType injuryType) {
		ModifiedInjuryContext newContext = context(injuryContext);
		T params = params(gameState, newContext, injuryType);
		if (tryArmourRollModification(params)) {
			if (modifyArmourInternal(params)) {
				newContext.setModification(InjuryModification.ARMOUR);
				newContext.setUsedSkill(skill);
				injuryContext.setModifiedInjuryContext(newContext);
				return true;
			}
		}
		return false;
	}

	protected boolean tryArmourRollModification(T params) {
		return !params.getNewContext().isArmorBroken();
	}

	protected boolean modifyArmourInternal(T params) {

		prepareArmourParams(params);

		if (armourModificationCantHelp(params)) {
			return false;
		}

		ModifiedInjuryContext newContext = params.getNewContext();
		newContext.clearArmorModifiers();
		applyArmourModification(params);
		newContext.setArmorBroken(params.getDiceInterpreter().isArmourBroken(params.getGameState(), newContext));

		return true;
	}

	protected abstract void prepareArmourParams(T params);

	protected abstract boolean armourModificationCantHelp(T params);

	protected abstract void applyArmourModification(T params);

	public boolean modifyInjury(InjuryContext injuryContext, GameState gameState) {
		if (!injuryContext.isCasualty()) {
			ModifiedInjuryContext newContext = context(injuryContext);
			if (modifyInjuryInternal(newContext, gameState)) {
				newContext.setModification(InjuryModification.INJURY);
				newContext.setUsedSkill(skill);
				injuryContext.setModifiedInjuryContext(newContext);
				return true;
			}
		}
		return false;
	}

	protected boolean modifyInjuryInternal(ModifiedInjuryContext injuryContext, GameState gameState) {
		return false;
	}

	@Override
	public boolean requiresConditionalReRollSkill() {
		return false;
	}

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

	private ModifiedInjuryContext context(InjuryContext context) {
		if (context.getModifiedInjuryContext() != null) {
			return context.getModifiedInjuryContext();
		}
		return newContext(context);
	}

	private ModifiedInjuryContext newContext(InjuryContext injuryContext) {
		ModifiedInjuryContext newContext = new ModifiedInjuryContext();
		newContext.fInjuryType = injuryContext.fInjuryType;
		newContext.fArmorModifiers.addAll(injuryContext.fArmorModifiers);
		newContext.fInjuryModifiers.addAll(injuryContext.fInjuryModifiers);
		newContext.casualtyModifiers.addAll(injuryContext.casualtyModifiers);
		newContext.fAttackerId = injuryContext.fAttackerId;
		newContext.fDefenderId = injuryContext.fDefenderId;
		newContext.fDefenderPosition = injuryContext.fDefenderPosition;
		newContext.fArmorBroken = injuryContext.fArmorBroken;
		newContext.fArmorRoll = new int[2];
		System.arraycopy(injuryContext.fArmorRoll, 0, newContext.fArmorRoll, 0, 2);
		if (injuryContext.fInjuryRoll != null) {
			newContext.fInjuryRoll = new int[2];
			System.arraycopy(injuryContext.fInjuryRoll, 0, newContext.fInjuryRoll, 0, 2);
		}
		newContext.fApothecaryMode = injuryContext.fApothecaryMode;
		newContext.fApothecaryStatus = injuryContext.fApothecaryStatus;
		newContext.setSkillUse(skillUse());
		return newContext;
	}
}
