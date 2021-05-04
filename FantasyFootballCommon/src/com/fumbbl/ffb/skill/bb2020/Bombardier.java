package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A coach may choose to have a Bombardier who is not Prone or Stunned throw a
 * bomb instead of taking any other Action with the player. This does not use
 * the team's Pass Action for the turn. The bomb is thrown using the rules for
 * throwing the ball (including weather effects), except that the player may not
 * move or stand up before throwing it (he needs time to light the fuse!).
 * Fumbled and intercepted bomb passes are not turnovers. All skills that may be
 * used when a ball is thrown may be used when a bomb is thrown also. A bomb may
 * be intercepted or caught using the same rules for catching the ball, in which
 * case the player catching it must throw it again immediately. This is a
 * special bonus Action that takes place out of the normal sequence of play. A
 * player holding the ball can catch or intercept and throw a bomb. The bomb
 * explodes when it lands in an empty square or an opportunity to catch the bomb
 * fails or is declined (i.e., bombs don't bounce). If the bomb is fumbled it
 * explodes in the bomb thrower's square. If a bomb lands in the crowd, it
 * explodes with no effect. When the bomb finally does explode any player in the
 * same square is Knocked Down, and players in adjacent squares are Knocked Down
 * on a roll of 4+. Players can be hit by a bomb and treated as Knocked Down
 * even if they are already Prone or Stunned. Make Armour and Injury rolls for
 * any players Knocked Down by the bomb. Casualties caused by a bomb do not
 * count for Star Player points.
 */
@RulesCollection(Rules.BB2020)
public class Bombardier extends Skill {

	public Bombardier() {
		super("Bombardier", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.enableThrowBombAction);
	}

}
