package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * When a player with this skill makes a Blitz Action, they can kick an adjacent
 * team-mate (who must have the Right Stuff skill) instead of throwing a Block.
 * No Block roll is made; instead, the target player is kicked as though they
 * were a ball! The coach declares whether they will roll a D6 for a short kick
 * or 2D6 for a riskier long kick. If they rolled 2D6 and scored a double, the
 * kicker has been a little too enthusiastic; make an Injury roll for the target
 * player, treating Stunned results as KO'd (if they were carrying the ball it
 * bounces from the square they were in).
 * 
 * Otherwise, the kicked player is moved (in a straight line) directly away from
 * the kicking player's square a number of squares equal to the total that was
 * rolled on the dice. Then they scatter three times. The kicked player does not
 * count as entering any square they move through except the one they end up in
 * after scattering. If the kicked player moves off the pitch, they land among
 * the crowd (never a pleasant fate!) and are sent to the KO'd box of the team's
 * Dugout. If they were carrying the ball, it will be thrown back on as normal,
 * starting from the last square the player moved through before leaving play.
 * 
 * If the final square they scatter into is occupied by another player, treat
 * the player landed on as Knocked Down and roll for Armour (even is already
 * Prone or Stunned), and then the player being kicked will scatter one more
 * square. If this moves them onto another player, continue to scatter them
 * until they end up in an empty square or off the pitch. Note that only the
 * first player they land on is Knocked Down.
 * 
 * Then see the Right Stuff entry to determine how gracefully the player lands -
 * where that skill refers to thrown players, it should be read as also
 * referring to kicked players. If the player moved 6, 7 or 8 squares (before
 * scattering), the Landing roll has a -1 modifier; if they moved 9 or more
 * (before scattering), the Landing roll has a -2 modifier.
 */
public class KickTeamMate extends Skill {

	public KickTeamMate() {
		super("Kick Team-Mate", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canKickTeamMates);
	}

}
