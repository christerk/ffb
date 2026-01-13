package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;


/**
 * If an opposition player suffers a Casualty as a result of a Special Action 
 * his player performed, this player will earn Star Player Points for causing 
 * a Casualty as appropriate.
 * A player can only have this Skill if they have a Trait that allows them 
 * to perform a Special Action.
 */
@RulesCollection(Rules.BB2025)
public class ViolentInnovator extends Skill {

	public ViolentInnovator() {
		super("Violent Innovator", SkillCategory.DEVIOUS);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.grantsSppFromSpecialActionsCas);
	}

}