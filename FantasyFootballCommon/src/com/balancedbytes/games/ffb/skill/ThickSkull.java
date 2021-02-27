package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.modifiers.InjuryModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * This player treats a roll of 8 on the Injury table, after any modifiers have
 * been applied, as a Stunned result rather than a KO'd result. This skill may
 * be used even if the player is Prone or Stunned.
 */
@RulesCollection(Rules.COMMON)
public class ThickSkull extends Skill {

	public ThickSkull() {
		super("Thick Skull", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.convertKOToStunOn8);
		registerModifier(new InjuryModifier("Thick Skull", 0, false));
	}

}
