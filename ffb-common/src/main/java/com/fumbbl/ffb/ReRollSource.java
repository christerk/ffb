package com.fumbbl.ffb;

import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;

public class ReRollSource implements INamedObject {

	private final String name;
	private final int priority;
	private final ReRollSource superior;

	ReRollSource(String name, ReRollSource superior) {
		this(name, 1, superior);
	}

	ReRollSource(String name, int priority) {
		this(name, priority, null);
	}

	ReRollSource(String pName) {
		this(pName, 1, null);
	}

	ReRollSource(String pName, int priority, ReRollSource superior) {
		name = pName;
		this.priority = priority;
		this.superior = superior;
	}

	public int getPriority() {
		return priority;
	}

	public String getName() {
		return name;
	}

	public Skill getSkill(Game game) {
		SkillFactory skillFactory = game.getRules().getSkillFactory();
		Skill skill = skillFactory.forName(name);
		if (skill == null && superior != null) {
			return superior.getSkill(game);
		}
		return skill;
	}

	public String getName(Game game) {
		Skill skill = getSkill(game);

		if (skill != null) {
			return skill.getName();
		}

		return name;
	}
}
