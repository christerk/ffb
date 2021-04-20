package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.skill.Skill;

public interface IRegistrationAwareModifier {
	boolean isRegisteredToSkillWithProperty(ISkillProperty property);

	void setRegisteredTo(Skill registeredTo);
}
