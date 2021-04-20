package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with the Sure Hands skill is allowed to re-roll the D6 if he fails
 * to pick up the ball. In addition, the Strip Ball skill will not work against
 * a player with this skill.
 */
@RulesCollection(Rules.COMMON)
public class SureHands extends Skill {

	public SureHands() {
		super("Sure Hands", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.forceOpponentToDropBallOnPushback));

		registerRerollSource(ReRolledActions.PICK_UP, ReRollSources.SURE_HANDS);
	}

}
