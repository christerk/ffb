package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.CatchContext;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;

/**
 * The player is superb at diving to catch balls others cannot reach and jumping
 * to more easily catch perfect passes. The player may add 1 to any catch roll
 * from an accurate pass targeted to his square. In addition, the player can
 * attempt to catch any pass , kick off or crowd throw-in, but not bouncing
 * ball, that would land in an empty square in one of his tackle zones as if it
 * had landed in his own square without leaving his current square. A failed
 * catch will bounce from the Diving Catch player's square. If there are two or
 * more players attempting to use this skill then they get in each other's way
 * and neither can use it.
 */
@RulesCollection(Rules.COMMON)
public class DivingCatch extends Skill {

	public DivingCatch() {
		super("Diving Catch", SkillCategory.AGILITY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canAttemptCatchInAdjacentSquares);
		registerProperty(NamedProperties.addBonusForAccuratePass);
		registerModifier(new CatchModifier("Diving Catch", -1, false, false) {
			@Override
			public boolean appliesToContext(Skill skill, CatchContext context) {

				return (CatchScatterThrowInMode.CATCH_ACCURATE_PASS == context.getCatchMode())
					|| (CatchScatterThrowInMode.CATCH_ACCURATE_BOMB == context.getCatchMode());

			}
		});
	}

}
