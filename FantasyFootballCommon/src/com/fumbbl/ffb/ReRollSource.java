package com.fumbbl.ffb;

import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;

public class ReRollSource implements INamedObject {

	private final String name;
	private Class<? extends Skill> skill;

	ReRollSource(String pName) {
		name = pName;
	}

	ReRollSource(Class<? extends Skill> skill) {
		this(skill.getName());
		this.skill = skill;
	}

	public String getName() {
		return name;
	}

	public Skill getSkill(Game game) {
		SkillFactory skillFactory = game.getRules().getSkillFactory();
		if (skill != null) {
			return skillFactory.forClass(skill);
		} else {
			return skillFactory.forName(name);
		}
	}

	public String getName(Game game) {
		Skill skill = getSkill(game);

		if (skill != null) {
			return skill.getName();
		}

		return name;
	}
}
