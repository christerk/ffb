package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.util.Scanner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UtilSkillBehaviours {

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void registerBehaviours(Game game, DebugLog log) {

		Scanner<SkillBehaviour> scanner = new Scanner<>(SkillBehaviour.class);
		Collection<SkillBehaviour> skillBehaviours = scanner.getSubclasses(game.getOptions());
		Set<String> packageNames = new HashSet<>();
		for (SkillBehaviour<Skill> behaviour : skillBehaviours) {
			packageNames.add(behaviour.getClass().getPackage().getName());
			if (registerBehaviour(behaviour, game.getRules().getSkillFactory())) {
				log.log(IServerLogLevel.DEBUG, game.getId(),
					"Registered behaviour class '" + behaviour.getClass().getSimpleName() + "' for skill '" + behaviour.skillClass.getSimpleName() + "'");
			} else {
				log.log(IServerLogLevel.WARN, game.getId(), "No skill found for '" + behaviour.getClass().getSimpleName());
			}
		}
		log.log(IServerLogLevel.INFO, game.getId(), "Loaded " + skillBehaviours.size() + " behaviours from these packages: " + String.join(", ", packageNames));
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
