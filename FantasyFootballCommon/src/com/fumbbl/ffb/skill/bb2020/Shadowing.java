package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * The player may use this s kill when a player performing an Action on the
 * opposing team moves out of any of his tackle zones for any reason. The
 * opposing player rolls 2D6 adding their own player's movement allowance and
 * subtracting the Shadowing player's movement allowance from the score. I f the
 * final result is 7 or less, the player with Shadowing may move into the square
 * vacated by the opposing player. He does not have to make any Dodge rolls when
 * he makes this move, and it has no effect on his own movement i n his own
 * turn. I f the final result is 8 or more, the opposing player successfully
 * avoids the Shadowing player and the Shadowing player is left standing. A
 * player may make any number of shadowing moves per turn. If a player has left
 * the tackle zone of several players that have the Shadowing s kill, then only
 * one of the opposing players may attempt to shadow him.
 */
@RulesCollection(Rules.BB2020)
public class Shadowing extends Skill {

	public Shadowing() {
		super("Shadowing", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canFollowPlayerLeavingTacklezones);
		registerConflictingProperty(NamedProperties.movesRandomly);
	}
}
