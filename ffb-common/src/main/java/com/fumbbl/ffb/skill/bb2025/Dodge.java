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
 * A player with the Dodge skill is adept at slipping away from opponents, and
 * is allowed to re-roll the D6 if he fails to dodge out of any of an opposing
 * player's tackle zones. However, the player may only re-roll one failed Dodge
 * roll per turn. In addition, the Dodge skill, if used, affects the results
 * rolled on the Block dice, as explained in the Blocking rules in the Blood
 * Bowl book.
 */
@RulesCollection(Rules.BB2025)
public class Dodge extends Skill {

	public Dodge() {
		super("Dodge", SkillCategory.AGILITY, SkillUsageType.ONCE_PER_TURN);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.ignoreDefenderStumblesResult);
		registerProperty(NamedProperties.canRerollDodge);
		registerRerollSource(ReRolledActions.DODGE, ReRollSources.DODGE);
	}

}
