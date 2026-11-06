package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.model.skill.Skill;

public class InjuryModificationCandidate {

	private final ChainableInjuryModification modification;
	private final String ownerId;
	private final InjuryPhase phase;

	public InjuryModificationCandidate(ChainableInjuryModification modification, String ownerId, InjuryPhase phase) {
		this.modification = modification;
		this.ownerId = ownerId;
		this.phase = phase;
	}

	public ChainableInjuryModification getModification() {
		return modification;
	}

	public Skill getSkill() {
		return modification.getSkill();
	}

	public String getOwnerId() {
		return ownerId;
	}

	public InjuryPhase getPhase() {
		return phase;
	}
}
