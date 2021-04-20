package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

/**
 * Titchy players tend to be even smaller and more nimble than other Stunty
 * players. To represent this, the player may add 1 to any Dodge roll he
 * attempts. On the other hand, while opponents do have to dodge to leave any of
 * a Titchy player's tackle zones, Titchy players are so small that they do not
 * exert a -1 modifier when opponents dodge into any of their tackle zones.
 */
@RulesCollection(Rules.COMMON)
public class Titchy extends Skill {

	public Titchy() {
		super("Titchy", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.hasNoTacklezone);
		registerModifier(new DodgeModifier("Titchy", -1, ModifierType.REGULAR));
	}

}
