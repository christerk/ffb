package com.fumbbl.ffb;

import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;

public class ReRollSource implements INamedObject {

	private final String name;
	private final int priority;

	ReRollSource(String pName) {
		this(pName, 1);
	}

	ReRollSource(String pName, int priority) {
		name = pName;
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public String getName() {
		return name;
	}

	public Skill getSkill(Game game) {
		SkillFactory skillFactory = game.getRules().getSkillFactory();
		return skillFactory.forName(name);
	}

	public String getName(Game game) {
		Skill skill = getSkill(game);

		if (skill != null) {
			return skill.getName();
		}

		return name;
	}
}
