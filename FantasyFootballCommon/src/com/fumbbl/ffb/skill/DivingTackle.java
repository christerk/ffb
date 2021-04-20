package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

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
		registerModifier(new DodgeModifier("Diving Tackle", 2, ModifierType.DIVING_TACKLE) {
			@Override
			public boolean appliesToContext(Skill skill, DodgeContext context) {
				return false;
			}
		});
	}
}
