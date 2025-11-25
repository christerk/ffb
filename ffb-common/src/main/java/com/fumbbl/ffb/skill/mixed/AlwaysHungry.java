package com.fumbbl.ffb.skill.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * The player is always ravenously hungry - and what's more they'll eat
 * absolutely anything! Should the player ever use the Throw Team-Mate skill,
 * roll a D6 after he has finished moving, but before he throws his team-mate.
 * On a 2+ continue with the throw. On a roll of 1 he attempts to eat the
 * unfortunate team-mate! Roll the D6 again, a second 1 means that he
 * successfully scoffs the team-mate down, which kills the team-mate without
 * opportunity for recovery (Apothecaries, Regeneration or anything else cannot
 * be used). If the team-mate had the ball it will scatter once from the
 * team-mate's square. If the second roll is 2-6 the team-mate squirms free and
 * the Pass Action is automatically treated as a fumbled pass. Fumble the player
 * with the Right Stuff skill as normal.
 */
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class AlwaysHungry extends Skill {

	public AlwaysHungry() {
		super("Always Hungry", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.mightEatPlayerToThrow);
	}
}
