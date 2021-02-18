package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.ReRollSources;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.ModifierDictionary;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * A player with the Dodge skill is adept at slipping away from opponents, and
 * is allowed to re-roll the D6 if he fails to dodge out of any of an opposing
 * player's tackle zones. However, the player may only re-roll one failed Dodge
 * roll per turn. In addition, the Dodge skill, if used, affects the results
 * rolled on the Block dice, as explained in the Blocking rules in the Blood
 * Bowl book.
 */
@RulesCollection(Rules.COMMON)
public class Dodge extends Skill {

	public Dodge() {
		super("Dodge", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct(ModifierDictionary dictionary) {
		registerProperty(NamedProperties.ignoreDefenderStumblesResult);

		registerRerollSource(ReRolledActions.DODGE, ReRollSources.DODGE);
	}

}
