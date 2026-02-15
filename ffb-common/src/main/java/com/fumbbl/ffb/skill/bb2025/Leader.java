package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * The player is a natural leader and commands the rest of the team from the
 * back-field as he prepares to throw the ball. A team with one or more players
 * with the Leader skill may take a single Leader Re-roll counter and add it to
 * their team reRolls at the start of the game and at half time after any Master
 * Chef rolls. The Leader re-roll is used exactly the same in every way as a
 * normal Team re-roll with all the same restrictions. In addition, the Leader
 * re-roll may only be used so long as at least one player with the Leader skill
 * is on the pitch - even if they are Prone or Stunned! Rerolls from Leader may
 * be carried over into Overtime if not used, but the team does not receive a
 * new Leader re-roll at the start of Overtime.
 */
@RulesCollection(Rules.BB2025)
public class Leader extends Skill {

	public Leader() {
		super("Leader", SkillCategory.PASSING, SkillUsageType.ONCE_PER_HALF);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.grantsTeamReRollWhenOnPitch);
	}

}
