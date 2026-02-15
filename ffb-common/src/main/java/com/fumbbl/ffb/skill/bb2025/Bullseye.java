package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;


/**
 * When this player performs a Throw Team-mate Action, if the result of the 
 * throw is a Superb Throw then the thrown player will not Scatter before 
 * landing and will instead land in the target square.
 * A player without the Throw Team-mate Trait cannot have this Skill.
 */
@RulesCollection(Rules.BB2025)
public class Bullseye extends Skill {

	public Bullseye() {
		super("Bullseye", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canSkipTtmScatterOnSuperbThrow);
	}

	@Override
	public boolean canBeAssignedTo(Player<?> player) {
		return player.hasSkillProperty(NamedProperties.canThrowTeamMates);
	}

}