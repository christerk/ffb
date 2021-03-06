package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.model.property.ISkillProperty;
import com.balancedbytes.games.ffb.model.Skill;

public abstract class RegistrationAwareModifier implements INamedObject {
	protected Skill registeredTo;

	public boolean isRegisteredToSkillWithProperty(ISkillProperty property) {
		return registeredTo != null && registeredTo.hasSkillProperty(property);
	}

	public void setRegisteredTo(Skill registeredTo) {
		this.registeredTo = registeredTo;
	}
}
