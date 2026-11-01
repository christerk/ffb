package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * State of a BB2025 armour / injury resolution: current context, phase, accepted modifications and the
 * modifiers added by them. The modifiers are tracked separately so they can be re-added after a base
 * evaluation of the injury type cleared the context modifiers.
 */
public class InjuryResolutionState {

	private final InjuryContext injuryContext;
	private InjuryPhase phase;
	private boolean armourReRolled;
	private boolean injuryReRolled;
	private final List<AppliedInjuryModification> appliedModifications = new ArrayList<>();
	private final Set<ArmorModifier> skillArmourModifiers = new LinkedHashSet<>();
	private final Set<InjuryModifier> skillInjuryModifiers = new LinkedHashSet<>();

	public InjuryResolutionState(InjuryContext injuryContext, InjuryPhase phase) {
		this.injuryContext = injuryContext;
		this.phase = phase;
	}

	public InjuryContext getInjuryContext() {
		return injuryContext;
	}

	public InjuryPhase getPhase() {
		return phase;
	}

	public void setPhase(InjuryPhase phase) {
		this.phase = phase;
	}

	public boolean isArmourReRolled() {
		return armourReRolled;
	}

	public boolean isInjuryReRolled() {
		return injuryReRolled;
	}

	public boolean isReRolled(InjuryPhase phase) {
		return (phase == InjuryPhase.ARMOUR && armourReRolled) || (phase == InjuryPhase.INJURY && injuryReRolled);
	}

	public void addAppliedModification(AppliedInjuryModification modification) {
		appliedModifications.add(modification);
		if (modification.getEffect() == ModificationEffect.RE_ROLL) {
			if (modification.getPhase() == InjuryPhase.ARMOUR) {
				armourReRolled = true;
			} else if (modification.getPhase() == InjuryPhase.INJURY) {
				injuryReRolled = true;
			}
		}
	}

	public List<AppliedInjuryModification> getAppliedModifications() {
		return Collections.unmodifiableList(appliedModifications);
	}

	public boolean isApplied(Skill skill, String playerId) {
		return appliedModifications.stream()
			.anyMatch(applied -> applied.getSkill() == skill && Objects.equals(applied.getPlayerId(), playerId));
	}

	public void addSkillArmourModifiers(Collection<ArmorModifier> modifiers) {
		skillArmourModifiers.addAll(modifiers);
	}

	public Set<ArmorModifier> getSkillArmourModifiers() {
		return Collections.unmodifiableSet(skillArmourModifiers);
	}

	public void addSkillInjuryModifiers(Collection<InjuryModifier> modifiers) {
		skillInjuryModifiers.addAll(modifiers);
	}

	public Set<InjuryModifier> getSkillInjuryModifiers() {
		return Collections.unmodifiableSet(skillInjuryModifiers);
	}
}
