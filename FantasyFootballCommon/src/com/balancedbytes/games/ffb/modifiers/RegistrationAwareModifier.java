package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.property.ISkillProperty;

public abstract class RegistrationAwareModifier implements IRegistrationAwareModifier {
	protected Skill registeredTo;

	@Override
	public boolean isRegisteredToSkillWithProperty(ISkillProperty property) {
		return registeredTo != null && registeredTo.hasSkillProperty(property);
	}

	@Override
	public void setRegisteredTo(Skill registeredTo) {
		this.registeredTo = registeredTo;
	}
}
