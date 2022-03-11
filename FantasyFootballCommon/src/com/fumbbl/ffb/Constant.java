package com.fumbbl.ffb;

import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kalimar
 */
public interface Constant {

	int MINIMUM_MOVE_TO_STAND_UP = 3;

	Set<ISkillProperty> CHECK_AFTER_PLAYER_REMOVAL = new HashSet<ISkillProperty>() {{
		add(NamedProperties.grantsSingleUseTeamRerollWhenOnPitch);
	}};
}
