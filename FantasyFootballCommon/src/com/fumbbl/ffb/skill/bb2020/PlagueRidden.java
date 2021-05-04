package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * This player has a horrible infectious disease which spreads when he kills an
 * opponent during a Block, Blitz or Foul Action. Instead of truly dying, the
 * infected opponent becomes a new rookie Rotter. To do so, the opponent must
 * have been removed from the roster during step 2.1 of the Post-game sequence,
 * his Strength cannot exceed 4, and he cannot have the Decay, Regeneration or
 * Stunty skills. The new Rotter can be added to the Nurgle team for free during
 * step 5 of Updating Your Team Roster (see page 29) if the team has an open
 * Roster slot. This new Rotter still counts at full value towards the total
 * value of the Nurgle team.
 */
@RulesCollection(Rules.BB2020)
public class PlagueRidden extends Skill {

	public PlagueRidden() {
		super("Plague Ridden", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.allowsRaisingLineman);

	}

}
