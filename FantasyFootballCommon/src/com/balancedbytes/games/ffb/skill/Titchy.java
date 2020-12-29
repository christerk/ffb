package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.DodgeModifiers;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * Titchy players tend to be even smaller and more nimble than other Stunty
 * players. To represent this, the player may add 1 to any Dodge roll he
 * attempts. On the other hand, while opponents do have to dodge to leave any of
 * a Titchy player's tackle zones, Titchy players are so small that they do not
 * exert a -1 modifier when opponents dodge into any of their tackle zones.
 */
@RulesCollection(Rules.All)
public class Titchy extends Skill {

	public Titchy() {
		super("Titchy", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.hasNoTacklezone);
		registerModifier(DodgeModifiers.TITCHY);
	}

}
