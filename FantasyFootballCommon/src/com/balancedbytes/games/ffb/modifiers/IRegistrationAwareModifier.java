package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.property.ISkillProperty;

public interface IRegistrationAwareModifier {
	boolean isRegisteredToSkillWithProperty(ISkillProperty property);

	void setRegisteredTo(Skill registeredTo);
}
