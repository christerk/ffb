package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;


/**
 * When an opposition player is Pushed Back by this player, the opposition 
 * player cannot provide Offensive or Defensive Assists until after they are 
 * next activated.
 */
@RulesCollection(Rules.BB2025)
public class EyeGouge extends Skill {

	public EyeGouge() {
		super("Eye Gouge", SkillCategory.DEVIOUS);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canRemoveOpponentAssists);
		registerConflictingProperty(NamedProperties.movesRandomly);
	}

}