package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * When a player with this Skill is Pushed Back as a result of a Block Action 
 * performed against the, this player’s Coach may choose to make the opposition 
 * player Follow-up.
 * This Skill cannot be used against an opposition player with the Take Root Trait 
 * that has become Rooted.
 */
@RulesCollection(Rules.BB2025)
public class Taunt extends Skill {

	public Taunt() {
		super("Taunt", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.forceOpponentToFollowUp);
	}

}