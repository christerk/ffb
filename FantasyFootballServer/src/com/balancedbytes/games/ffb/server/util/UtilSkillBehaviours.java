package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.util.Scanner;

public class UtilSkillBehaviours {

	@SuppressWarnings("unchecked")
	public static void registerBehaviours(Game game, DebugLog log) {

		Scanner<SkillBehaviour> scanner = new Scanner<SkillBehaviour>(SkillBehaviour.class);
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
