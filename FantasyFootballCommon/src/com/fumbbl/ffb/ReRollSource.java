package com.fumbbl.ffb;

import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * 
 * @author Kalimar
 */
public class ReRollSource implements INamedObject {

	private int id;
	private String name;
	private Class<? extends Skill> skill;

	ReRollSource(String pName) {
		name = pName;
	}

	ReRollSource(Class<? extends Skill> skill) {
		this(skill.getName());
		this.skill = skill;
	}

	public int getId() {
		return id;
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

}
