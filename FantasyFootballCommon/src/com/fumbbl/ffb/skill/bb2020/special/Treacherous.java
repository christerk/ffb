package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
/**
* Once per game, if a team-mate in an adjacent square to Hakflem is in possession of the ball when
* Hakflem is activated, that player may immediately be Knocked Down and Hakflem may take possession of the ball.
* No Turnover is cause as a result of using this special rule

*/

@RulesCollection(Rules.BB2020)
public class Treacherous extends Skill {
	public Treacherous() {
		super("Treacherous", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}
}
