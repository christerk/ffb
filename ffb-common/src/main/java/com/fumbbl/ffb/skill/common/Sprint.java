package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * The player may attempt to move up to three extra squares rather than the
 * normal two when Going For It (see page 20). His coach must still roll to see
 * if the player is Knocked Down in each extra square he enters.
 */
@RulesCollection(Rules.COMMON)
public class Sprint extends Skill {

	public Sprint() {
		super("Sprint", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canMakeAnExtraGfi);
	}

}
