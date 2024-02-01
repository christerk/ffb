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
		SkillFactory skillFactory = game.getRules().getSkillFactory();
		for (SkillBehaviour<Skill> behaviour : skillBehaviours) {
			packageNames.add(behaviour.getClass().getPackage().getName());
			log.log(IServerLogLevel.DEBUG, game.getId(), "Using skillFactory: " + skillFactory);
			registerBehaviour(behaviour, skillFactory, log, game.getId());
		}
		log.log(IServerLogLevel.INFO, game.getId(), "Loaded " + skillBehaviours.size() + " behaviours from these packages: " + String.join(", ", packageNames));
	}

	private static void registerBehaviour(SkillBehaviour<Skill> behaviour, SkillFactory factory, DebugLog log, long gameId) {
		Skill skill = factory.forClass(behaviour.skillClass);
		if (skill != null) {
			log.log(IServerLogLevel.DEBUG, gameId,
				"Registered behaviour class '" + behaviour.getClass().getSimpleName() + " " + behaviour + "' for skill '"
					+ behaviour.skillClass.getSimpleName() + " " + skill.superString() +"'");
			behaviour.setSkill(skill);
			return;
		}
		log.log(IServerLogLevel.WARN, gameId, "No skill found for '" + behaviour.getClass().getSimpleName());
	}
}
