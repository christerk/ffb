package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.skill.Skill;

public abstract class RegistrationAwareModifier implements IRegistrationAwareModifier {
	protected Skill registeredTo;

	@Override
	public Skill getRegisteredTo() {
		return registeredTo;
	}

	@Override
	public boolean isRegisteredToSkillWithProperty(ISkillProperty property) {
		return registeredTo != null && registeredTo.hasSkillProperty(property);
	}

	@Override
	public void setRegisteredTo(Skill registeredTo) {
		this.registeredTo = registeredTo;
	}
}
