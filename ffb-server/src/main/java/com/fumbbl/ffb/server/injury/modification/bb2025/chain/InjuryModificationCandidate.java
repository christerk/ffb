package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

public class InjuryModificationCandidate {

	private final Player<?> player;
	private final ChainableInjuryModification modification;

	public InjuryModificationCandidate(Player<?> player, ChainableInjuryModification modification) {
		this.player = player;
		this.modification = modification;
	}

	public Player<?> getPlayer() {
		return player;
	}

	public ChainableInjuryModification getModification() {
		return modification;
	}

	public Skill getSkill() {
		return modification.getSkill();
	}
}
