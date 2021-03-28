package com.balancedbytes.games.ffb.model.property;

import com.balancedbytes.games.ffb.model.skill.Skill;

import java.util.Objects;

public class CancelSkillProperty implements ISkillProperty {
	private final ISkillProperty cancelledProperty;

	public CancelSkillProperty(ISkillProperty cancelledProperty) {
		this.cancelledProperty = cancelledProperty;
	}

	public boolean cancelsSkill(Skill skill) {
		return skill.getSkillProperties().contains(cancelledProperty);
	}

	public boolean cancelsProperty(ISkillProperty property) {
		return property.equals(cancelledProperty);
	}

	@Override
	public String getName() {
		return "cancel" + cancelledProperty.getName();
	}

	@Override
	public int hashCode() {
		return Objects.hash(cancelledProperty);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof CancelSkillProperty && ((CancelSkillProperty) other).cancelledProperty.equals(cancelledProperty);
	}
}
