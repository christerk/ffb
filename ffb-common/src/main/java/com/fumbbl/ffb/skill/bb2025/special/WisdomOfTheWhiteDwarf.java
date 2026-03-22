package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per game, when Grombrindal is activated he may select one team-mate within 2 squares. 
 * The selected team-mate gains one of the following Skills until the end of the turn: 
 * Break Tackle, Dauntless, Mighty Blow, Sure Feet.
 */

@RulesCollection(Rules.BB2025)
public class WisdomOfTheWhiteDwarf extends Skill {
	public WisdomOfTheWhiteDwarf() {
		super("Wisdom of the White Dwarf", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canGrantSkillsToTeamMates);
	}

	@Override
	public String enhancementSourceName() {
		return "Granted by Wisdom of the White Dwarf";
	}
}
