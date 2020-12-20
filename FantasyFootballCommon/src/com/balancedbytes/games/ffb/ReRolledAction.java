package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class ReRolledAction implements INamedObject {

	private String name;
	private Class<? extends Skill> skill;

	ReRolledAction(String pName) {
		this.name = pName;
		this.skill = null;
	}

	ReRolledAction(Class<? extends Skill> skill) {
		this.skill = skill;
		this.name = skill.getName();
	}

	public String getName() {
		return name;
	}

	public Skill getSkill(SkillFactory skillFactory) {
		if (skill != null) {
			return skillFactory.forClass(skill);
		} else {
			return skillFactory.forName(name);
		}
	}

}
