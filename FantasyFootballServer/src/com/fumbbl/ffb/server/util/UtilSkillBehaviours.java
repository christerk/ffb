package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.util.Scanner;

public class UtilSkillBehaviours {

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void registerBehaviours(Game game, DebugLog log) {

		Scanner<SkillBehaviour> scanner = new Scanner<>(SkillBehaviour.class);
		for (SkillBehaviour<Skill> behaviour : scanner.getSubclasses(game.getOptions())) {
			if (registerBehaviour(behaviour, game.getRules().getSkillFactory())) {
				log.log(IServerLogLevel.DEBUG,
						"Registered behavior class '" + behaviour.getClass().getSimpleName() + "' for skill '" + behaviour.skillClass.getSimpleName() + "'");
			} else {
				log.log(IServerLogLevel.WARN, "No skill found for '" + behaviour.getClass().getSimpleName());
			}
		}
	}

	private static boolean registerBehaviour(SkillBehaviour<Skill> behaviour, SkillFactory factory) {
		Skill skill = factory.forClass(behaviour.skillClass);
		if (skill != null) {
			behaviour.setSkill(skill);
			return true;
		}
		return false;
	}
}
