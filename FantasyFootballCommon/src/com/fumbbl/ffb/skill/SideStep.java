package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with this skill is an expert at stepping neatly out of the way of an
 * attacker. To represent this ability, his coach may choose which square the
 * player is moved to when he is pushed back, rather than the opposing coach.
 * Furthermore, the coach may choose to move the player to any adjacent square,
 * not just the three squares shown on the Push Back diagram. Note that the
 * player may not use this skill if there are no open squares on the pitch
 * adjacent to this player. Note that the coach may choose which square the
 * player is moved to even if the player is Knocked Down after the push back.
 */
@RulesCollection(Rules.COMMON)
public class SideStep extends Skill {

	public SideStep() {
		super("Side Step", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.canPushBackToAnySquare));

		registerProperty(NamedProperties.canChooseOwnPushedBackSquare);
	}

	@Override
	public String[] getSkillUseDescription() {
		return new String[] { "Using SideStep will allow you to chose the square you are pushed to." };
	}
}
