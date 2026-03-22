package com.fumbbl.ffb;

import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.skill.mixed.special.BalefulHex;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Constant {

	public static int REPLAY_NAME_MAX_LENGTH = 20;

	public static int MINIMUM_MOVE_TO_STAND_UP = 3;

	public static final Set<ISkillProperty> CHECK_AFTER_PLAYER_REMOVAL = new HashSet<ISkillProperty>() {{
		add(NamedProperties.grantsSingleUseTeamRerollWhenOnPitch);
	}};

	public static Set<SkillWithValue> getGrantAbleSkills(SkillFactory skillFactory) {
		return GRANT_ABLE_SKILLS.stream()
			.map(entry -> new SkillWithValue(skillFactory.forName(entry[0]), entry[1]))
			.filter(swv -> swv.getSkill() != null)
			.collect(Collectors.toSet());
	}

	private static final Set<String[]> GRANT_ABLE_SKILLS = new HashSet<String[]>() {{
		add(new String[] { "Break Tackle", null });
		add(new String[] { "Dauntless", null });
		add(new String[] { "Mighty Blow", "1" });
		add(new String[] { "Sure Feet", null });
	}};

	public static Set<String> getEnhancementSkillsToRemoveAtEndOfTurnWhenNotSettingActive(SkillFactory skillFactory) {
		return new HashSet<Class<? extends Skill>>() {{
			add(BalefulHex.class);
		}}.stream().map(skillFactory::forClass).map(Skill::getName).collect(Collectors.toSet());
	}
}
