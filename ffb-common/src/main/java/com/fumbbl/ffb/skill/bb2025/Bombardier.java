package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * When this player is activated, they can declare a Throw Bomb Special Action 
 * only one player can declare this Special Action each Turn.
 * When a player performs a Throw Bomb Special Action, they throw a bomb in 
 * the same manner as when a player performs a Pass Action, following all the 
 * usual rules for a Pass Action. Though this is not a Pass Action itself, any 
 * Skills or Traits that come into play when a player performs a Pass Action will 
 * also apply to a Throw Bomb Special Action, with the exception of the On the 
 * Ball Skill A player that declared a Tow Bomb Special Action may not perform 
 * a Move Action before throwing the bomb.
 * If at any point a bomb comes to rest on the ground then it will immediately 
 * explode in that square. Should a bomb be Fumbled by the thrower, or dropped 
 * when a player attempts to Catch it, then it will not Bounce and will instead 
 * explode in that player’s square. When a bomb explodes, any player in the square 
 * it exploded in is hit by the explosion. Additionally, roll a d6 for each player 
 * adjacent to the square in which the bomb exploded. O a 4+ they are hit by the 
 * explosion.
 * Any Standing player that is hit by the explosion is immediately Knocked Down. 
 * Additionally, make an Amrour Roll for any Prone or Stunned players hit by the 
 * explosion.
 * If a player successfully Catches or Intercepts a thrown bomb, the player that 
 * caught the bomb must immediately throw it again, following all the same rules 
 * for making  a throw Bomb Special Action as described above.
 */
@RulesCollection(Rules.BB2025)
public class Bombardier extends Skill {

	public Bombardier() {
		super("Bombardier", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.enableThrowBombAction);
	}

}
