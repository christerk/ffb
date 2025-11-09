package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * This player has the quickness and finesse to stick the boot to a downed
 * opponent without drawing a referee's attention unless he hears the armour
 * crack. During a Foul Action a player with this skill is not ejected for
 * rolling doubles on the Armour roll unless the Armour roll was successful.
 */
@RulesCollection(Rules.BB2020)
public class SneakyGit extends Skill {

	public SneakyGit() {
		super("Sneaky Git", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canAlwaysAssistFoulsWithSg);
		registerProperty(NamedProperties.canMoveAfterFoul);
	}

}
