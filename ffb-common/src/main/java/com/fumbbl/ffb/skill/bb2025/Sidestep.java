package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * Whenever this player is Pushed Back for any reason, then instead of the 
 * opposing Coach choosing where this player is Pushed Back to, this player’s 
 * Coach may choose any adjacent unoccupied square for this player to be Pushed 
 * Back into instead. If there are no adjacent unoccupied squares, then this 
 * Skill cannot be used.
 */
@RulesCollection(Rules.BB2025)
public class Sidestep extends Skill {

	public Sidestep() {
		super("Sidestep", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canChooseOwnPushedBackSquare);
	}

	@Override
	public String[] getSkillUseDescription() {
		return new String[] { "Using Sidestep will allow you to chose the square you are pushed to." };
	}
}
