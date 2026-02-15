package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * When this player is thrown by a Throw Team-mate Action, they may choose not 
 * to Scatter before landing as normal. If they do, position the Throw-in 
 * Template over this player so it faces one of the two End Zones or either 
 * Sideline. Roll a D6 to determine the direction this player will travel, and 
 * then a second D6 to determine how many squares in that direction this player 
 * will travel.
 * Additionally, if they choose not to Scatter as normal, this player may re-roll 
 * the Agility Test when attempting to land.
 */
@RulesCollection(Rules.BB2025)
public class Swoop extends Skill {

	public Swoop() {
		super("Swoop", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_TURN_BY_TEAM_MATE);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.ttmScattersInSingleDirection);
		registerRerollSource(ReRolledActions.RIGHT_STUFF, ReRollSources.SWOOP);
	}	

}
