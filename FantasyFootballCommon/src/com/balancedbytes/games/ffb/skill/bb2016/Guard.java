package com.balancedbytes.games.ffb.skill.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

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
