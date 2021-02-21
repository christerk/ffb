package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.modifiers.PassModifier;
import com.balancedbytes.games.ffb.modifiers.PassContext;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * A player with this skill has the ability to throw a player from the same team
 * instead of the ball! (This includes the ball if the player thrown already has
 * it!) The player throwing must end the movement of his Pass Action standing
 * next to the intended team-mate to be thrown, who must have the Right Stuff
 * skill and be standing. The pass is worked out exactly the same as the player
 * with Throw Team-Mate passing a ball, except the player must subtract 1 from
 * the D6 roll when he passes the player, fumbles are not automatically
 * turnovers, and Long Pass or Long Bomb range passes are not possible. In
 * addition, accurate passes are treated instead as inaccurate passes thus
 * scattering the player three times as players are heavier and harder to pass
 * than a ball. The thrown player cannot be intercepted. A fumbled team-mate
 * will land in the square he originally occupied. If the thrown player scatters
 * off the pitch, he is beaten up by the crowd in the same manner as a player
 * who has been pushed off the pitch. If the final square he scatters into is
 * occupied by another player, treat the player landed on as Knocked Down and
 * roll for Armour (even if already Prone or Stunned), and then the player being
 * thrown will scatter one more square. If the thrown player would land on
 * another player, continue to scatter the thrown player until he ends up in an
 * empty square or off the pitch (i.e. he cannot land on more than one player).
 * See the Right Stuff entry to see if the player lands on his feet or head-down
 * in a crumpled heap!
 */
@RulesCollection(Rules.COMMON)
public class ThrowTeamMate extends Skill {

	public ThrowTeamMate() {
		super("Throw Team-Mate", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canThrowTeamMates);
		registerModifier(new PassModifier("Throw Team-Mate", 1, false, false) {
			@Override
			public boolean appliesToContext(PassContext context) {
				return context.isDuringThrowTeamMate();
			}
		});
	}

}
