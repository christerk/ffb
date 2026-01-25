package com.fumbbl.ffb.skill.bb2025.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;

import java.util.Collections;

/**
 * Once per game, when Gretchen is activated she can use this special rule. 
 * Until the end of her activation, Gretchen does not have to make Dodge 
 * rolls for leaving a square within an opposition player’s Tackle Zone.
 */

@RulesCollection(Rules.BB2025)
public class Incorporeal extends Skill {
	public Incorporeal() {
		super("Incorporeal", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canAvoidDodging);
		setEnhancements(new TemporaryEnhancements().withProperties(Collections.singleton(NamedProperties.ignoreTacklezonesWhenMoving)));
	}

}
