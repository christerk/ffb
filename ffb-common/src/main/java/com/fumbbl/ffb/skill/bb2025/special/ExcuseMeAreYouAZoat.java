package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;


/**
 * Once per game, when Zolcath is activated he may select an opposition player 
 * within 3 squares. The selected player immediately loses their Tackle Zone 
 * until they are next activated.
 */

@RulesCollection(Rules.BB2025)
public class ExcuseMeAreYouAZoat extends Skill {
	public ExcuseMeAreYouAZoat() {
		super("\"Excuse Me, Are You a Zoat?\"", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canGazeAutomaticallyThreeSquaresAway);
	}

}
