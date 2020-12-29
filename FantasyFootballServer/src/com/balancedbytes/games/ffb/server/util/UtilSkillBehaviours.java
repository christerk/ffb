package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.util.Scanner;

import java.lang.reflect.InvocationTargetException;

public class UtilSkillBehaviours {

	public static void registerBehaviours(SkillFactory factory, DebugLog log) {

		Scanner<SkillBehaviour> scanner = new Scanner<>(SkillBehaviour.class);
		for (Class<SkillBehaviour> behaviourClass : scanner.getSubclasses()) {
			
			try {
				@SuppressWarnings("unchecked")
				SkillBehaviour<Skill> behaviour = (SkillBehaviour<Skill>) behaviourClass.getConstructor((Class<Skill>[]) null)
						.newInstance((Object[]) null);
				if (registerBehaviour(behaviour, factory)) {
					log.log(IServerLogLevel.DEBUG,
							"Registered behavior class '" + behaviourClass.getSimpleName() + "' for skill '" + behaviour.skillClass.getSimpleName() + "'");
				} else {
					log.log(IServerLogLevel.WARN, "No skill found for '" + behaviour.getClass().getSimpleName());
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				log.log(IServerLogLevel.WARN, "Failed to register behaviour for '" + behaviourClass.getSimpleName() + "': " + e.getMessage());
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
