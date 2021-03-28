package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

/**
 * The player is not noted for his intelligence. Because of this you must roll a
 * D6 immediately after declaring an Action for the player, but before taking
 * the Action. On a roll of 1 they stand around trying to remember what it is
 * they're meant to be doing. The player can't do anything for the turn, and the
 * player's team loses the declared Action for the turn. (So if a Bone-head
 * player declares a Blitz Action and rolls a 1, then the team cannot declare
 * another Blitz Action that turn.) The player loses his tackle zones and may
 * not catch, intercept or pass, assist another player on a block or foul, or
 * voluntarily move until he manages to roll a 2 or better at the start of a
 * future Action or the drive ends.
 */
@RulesCollection(Rules.COMMON)
public class BoneHead extends Skill {

	public BoneHead() {
		super("Bone-Head", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.appliesConfusion);
	}

}
