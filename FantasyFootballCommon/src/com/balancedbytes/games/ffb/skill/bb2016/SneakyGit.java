package com.balancedbytes.games.ffb.skill.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

/**
 * This player has the quickness and finesse to stick the boot to a downed
 * opponent without drawing a referee's attention unless he hears the armour
 * crack. During a Foul Action a player with this skill is not ejected for
 * rolling doubles on the Armour roll unless the Armour roll was successful.
 */
@RulesCollection(Rules.BB2016)
public class SneakyGit extends Skill {

	public SneakyGit() {
		super("Sneaky Git", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canAlwaysAssistFouls);
	}

}
