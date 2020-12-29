package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * A player with this skill may choose to not be pushed back as the result of a
 * block. He may choose to ignore being pushed by "Pushed" results, and to have
 * 'Knock-down' results knock the player down in the square where he started. If
 * a player is pushed back into a player with using Stand Firm then neither
 * player moves.
 */
@RulesCollection(Rules.All)
public class StandFirm extends Skill {

	public StandFirm() {
		super("Stand Firm", SkillCategory.STRENGTH);
	}

}
