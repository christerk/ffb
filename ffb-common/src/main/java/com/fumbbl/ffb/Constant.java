package com.fumbbl.ffb;

import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.skill.Dauntless;
import com.fumbbl.ffb.skill.bb2020.BreakTackle;
import com.fumbbl.ffb.skill.bb2020.MightyBlow;
import com.fumbbl.ffb.skill.bb2020.SureFeet;
import com.fumbbl.ffb.skill.bb2020.special.BalefulHex;
import com.fumbbl.ffb.skill.bb2020.special.WisdomOfTheWhiteDwarf;

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
		return GRANT_ABLE_SKILLS.stream().map(scwv ->
				new SkillWithValue(skillFactory.forClass(scwv.getSkill()), scwv.getValue().orElse(null)))
			.collect(Collectors.toSet());
	}

	private static final Set<SkillClassWithValue> GRANT_ABLE_SKILLS = new HashSet<SkillClassWithValue>() {{
		add(new SkillClassWithValue(BreakTackle.class));
		add(new SkillClassWithValue(Dauntless.class));
		add(new SkillClassWithValue(MightyBlow.class, "1"));
		add(new SkillClassWithValue(SureFeet.class));
	}};

	public static Set<String> getEnhancementSkillsToRemoveAtEndOfTurn(SkillFactory skillFactory) {
		return new HashSet<Class<? extends Skill>>() {{
			add(WisdomOfTheWhiteDwarf.class);
		}}.stream().map(skillFactory::forClass).map(Skill::getName).collect(Collectors.toSet());
	}

	public static Set<String> getEnhancementSkillsToRemoveAtEndOfTurnWhenNotSettingActive(SkillFactory skillFactory) {
		return new HashSet<Class<? extends Skill>>() {{
			add(BalefulHex.class);
		}}.stream().map(skillFactory::forClass).map(Skill::getName).collect(Collectors.toSet());
	}

}
