package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, if Morg fails the Passing Ability test when making a Pass action or a Throw Team-mate action, you may re-roll the D6
 */

@RulesCollection(Rules.BB2020)
public class TheBallista extends Skill {
	public TheBallista() {
		super("The Ballista", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerRerollSource(ReRolledActions.PASS, ReRollSources.THE_BALLISTA);
		registerRerollSource(ReRolledActions.THROW_TEAM_MATE, ReRollSources.THE_BALLISTA);
	}
}
