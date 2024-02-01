package com.fumbbl.ffb.tools.mock;

import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;

public class MockSkillFactory extends SkillFactory {

	@Override
	public Skill forName(String name) {
		return null;
	}
}
