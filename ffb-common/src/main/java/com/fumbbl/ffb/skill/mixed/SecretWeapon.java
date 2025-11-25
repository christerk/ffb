package com.fumbbl.ffb.skill.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillValueEvaluator;

/**
 * Some players are armed with special pieces of equipment that are called
 * "secret weapons". Although the Blood Bowl rules specifically ban the use of
 * any weapons, the game has a long history of teams trying to get weapons of
 * some sort onto the pitch. Nonetheless, the use of secret weapons is simply
 * not legal, and referees have a nasty habit of sending off players that use
 * them. Once a drive ends that this player has played in at any point, the
 * referee orders the player to be sent off to the dungeon to join players that
 * have been caught committing fouls during the match regardless of whether the
 * player is still on the pitch or not.
 */
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class SecretWeapon extends Skill {

	public SecretWeapon() {
		super("Secret Weapon", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.getsSentOffAtEndOfDrive);
	}

	@Override
	public SkillValueEvaluator evaluator() {
		return SkillValueEvaluator.ROLL;
	}
}
