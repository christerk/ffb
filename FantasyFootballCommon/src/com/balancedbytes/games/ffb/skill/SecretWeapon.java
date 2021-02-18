package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.ModifierDictionary;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

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
@RulesCollection(Rules.COMMON)
public class SecretWeapon extends Skill {

	public SecretWeapon() {
		super("Secret Weapon", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct(ModifierDictionary dictionary) {
		registerProperty(NamedProperties.preventStuntyDodgeModifier);
		registerProperty(NamedProperties.getsSentOffAtEndOfDrive);
	}

}
