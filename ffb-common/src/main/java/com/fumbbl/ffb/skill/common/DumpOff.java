package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * This skill allows the player to make a Quick Pass when an opponent declares
 * that he will throw a block at him, allowing the player to get rid of the ball
 * before he is hit. Work out the Dump-Off pass before the opponent makes his
 * block. The normal throwing rules apply, except that neither team's turn ends
 * as a result of the throw, whatever it may be. After the throw is worked out
 * your opponent completes the block, and then carries on with his turn.
 * Dump-Off may not be used on the second block from an opponent with the Frenzy
 * skill or in conjunction with the Bombardier or Throw Team-Mate skills.
 */
@RulesCollection(Rules.COMMON)
public class DumpOff extends Skill {

	public DumpOff() {
		super("Dump-Off", SkillCategory.PASSING);
	}

}
