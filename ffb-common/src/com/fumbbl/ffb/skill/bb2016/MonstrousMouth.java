package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with a Monstrous Mouth is allowed to re-roll the D6 if they fail a
 * Catch roll. It also allows the player to re-roll the D6 if they drop a
 * hand-off or fail to make an interception. In addition, the Strip Ball skill
 * will not work against a player with a Monstrous Mouth.
 */
@RulesCollection(Rules.BB2016)
public class MonstrousMouth extends Skill {

	public MonstrousMouth() {
		super("Monstrous Mouth", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.forceOpponentToDropBallOnPushback));
		
		registerRerollSource(ReRolledActions.CATCH, ReRollSources.MONSTROUS_MOUTH);
	}

}
