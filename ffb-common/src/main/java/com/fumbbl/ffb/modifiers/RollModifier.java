package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.model.skill.Skill;

public abstract class RollModifier<C extends ModifierContext> implements INamedObject {

	public abstract int getModifier();

	public abstract boolean isModifierIncluded();

	public abstract String getReportString();

	public int getMultiplier() {
		return getModifier();
	}

	public boolean appliesToContext(Skill skill, C context) {
		return true;
	}

	public abstract ModifierType getType();

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == this.getClass()
			&& ((RollModifier<?>)other).getName().equals(getName());
	}
}
