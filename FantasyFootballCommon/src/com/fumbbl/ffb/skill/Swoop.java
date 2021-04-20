package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.RightStuffModifier;

/**
 * This player is equipped with a rudimentary set of wings, either natural or
 * engineered, allowing them to glide through the air (rather than plummeting
 * gracelessly) if they are thrown by a team-mate. If a player with Swoop is
 * thrown by a player with the Throw Team-mate skill, the Throw-in template is
 * used instead of the Scatter template to see where they land. Each time the
 * player scatters, their coach places the Throw-in template over the player
 * facing up or down the pitch or towards either sideline. Then they roll a D6
 * and move the player one square in the indicated direction. In addition, when
 * rolling to see whether the player lands on their feet (as per the Right Stuff
 * skill), add 1 to the result. When a player with both the Swoop and Stunty
 * skills dodges, they do not ignore any modifiers for enemy tackle zones on the
 * square they are moving to - the presence of a large pair of wings negates any
 * benefit they would gain from being small and slippery.
 */
@RulesCollection(Rules.COMMON)
public class Swoop extends Skill {

	public Swoop() {
		super("Swoop", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.preventStuntyDodgeModifier);
		registerProperty(NamedProperties.ttmScattersInSingleDirection);
		registerProperty(new CancelSkillProperty(NamedProperties.ignoreTacklezonesWhenDodging));
		registerModifier(new RightStuffModifier("Swoop", -1, ModifierType.REGULAR));
	}

}
