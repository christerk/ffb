package com.fumbbl.ffb.skill;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with this skill is virtually impossible to stop once he is in
 * motion. If this player takes a Blitz Action, then opposing players may not
 * use their Fend, Stand Firm or Wrestle skills against blocks, and he may
 * choose to treat a "Both Down" result as if a "Pushed" result has been rolled
 * instead.
 */
@RulesCollection(Rules.COMMON)
public class Juggernaut extends Skill {

	public Juggernaut() {
		super("Juggernaut", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.canTakeDownPlayersWithHimOnBothDown));
		registerProperty(new CancelSkillProperty(NamedProperties.canRefuseToBePushed));
		registerProperty(new CancelSkillProperty(NamedProperties.preventOpponentFollowingUp));

	}

	@Override
	public String[] getSkillUseDescription() {
		return new String[] { "Using Juggernaut will convert the BOTH DOWN Block Result into a PUSHBACK." };
	}
}
