package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with this skill assists an offensive or defensive block even if he
 * is in another player's tackle zone. This skill may not be used to assist a
 * foul.
 */
@RulesCollection(Rules.BB2016)
public class Guard extends Skill {

	public Guard() {
		super("Guard", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.assistsBlocksInTacklezones);
	}

}
