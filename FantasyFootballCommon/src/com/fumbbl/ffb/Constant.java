package com.fumbbl.ffb;

import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;
import com.fumbbl.ffb.skill.Dauntless;
import com.fumbbl.ffb.skill.SureFeet;
import com.fumbbl.ffb.skill.bb2020.BreakTackle;
import com.fumbbl.ffb.skill.bb2020.MightyBlow;

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

	Set<SkillClassWithValue> GRANT_ABLE_SKILLS = new HashSet<SkillClassWithValue>() {{
		add(new SkillClassWithValue(BreakTackle.class));
		add(new SkillClassWithValue(Dauntless.class));
		add(new SkillClassWithValue(MightyBlow.class, "1"));
		add(new SkillClassWithValue(SureFeet.class));
	}};
}
