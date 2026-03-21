package com.fumbbl.ffb.server.mechanic;

import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.List;

public class ReRollOptions {
	private final List<ReRollProperty> properties;
	private final Skill reRollSkill;
	private final boolean canActuallyReRoll;

	public ReRollOptions(List<ReRollProperty> properties, Skill reRollSkill) {
		this.properties = properties;
		this.reRollSkill = reRollSkill;
		this.canActuallyReRoll = properties.stream().anyMatch(ReRollProperty::isActualReRoll) || reRollSkill != null;
	}

	public List<ReRollProperty> getProperties() {
		return properties;
	}

	public Skill getReRollSkill() {
		return reRollSkill;
	}

	public boolean canActuallyReRoll() {
		return canActuallyReRoll;
	}
}
