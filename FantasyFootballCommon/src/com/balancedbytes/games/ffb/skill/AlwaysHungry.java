package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * The player is always ravenously hungry - and what's more they'll eat
 * absolutely anything! Should the player ever use the Throw Team-Mate skill,
 * roll a D6 after he has finished moving, but before he throws his team-mate.
 * On a 2+ continue with the throw. On a roll of 1 he attempts to eat the
 * unfortunate team-mate! Roll the D6 again, a second 1 means that he
 * successfully scoffs the team-mate down, which kills the team-mate without
 * opportunity for recovery (Apothecaries, Regeneration or anything else cannot
 * be used). If the team-mate had the ball it will scatter once from the
 * team-mate's square. If the second roll is 2-6 the team-mate squirms free and
 * the Pass Action is automatically treated as a fumbled pass. Fumble the player
 * with the Right Stuff skill as normal.
 */
public class AlwaysHungry extends Skill {

	public AlwaysHungry() {
		super("Always Hungry", SkillCategory.EXTRAORDINARY);
	}

}
