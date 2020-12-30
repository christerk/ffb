package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * The player may use this skill after an opposing player attempts to dodge out
 * of any of his tackle zones. The player using this skill is Placed Prone in
 * the square vacated by the dodging player, but do not make an Armour or Injury
 * roll for them. The opposing player must then subtract 2 from his Dodge roll
 * for leaving the player's tackle zone. If a player is attempting to leave the
 * tackle zone of several players that have the Diving Tackle skill, then only
 * one of the opposing players may use Diving Tackle. Diving Tackle may be used
 * on a re-rolled dodge if not declared for use on the first Dodge roll. In
 * addition, if Diving Tackle is used on the 1st Dodge roll, both the -2
 * modifier and tackle zone still apply to the Dodge re-roll.
 */
@RulesCollection(Rules.COMMON)
public class DivingTackle extends Skill {

	public DivingTackle() {
		super("Diving Tackle", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canAttemptToTackleDodgingPlayer);
	}
}
