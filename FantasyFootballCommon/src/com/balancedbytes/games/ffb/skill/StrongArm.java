package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.PassingModifiers.PassContext;
import com.balancedbytes.games.ffb.model.ModifierDictionary;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * The player may add 1 to the D6 when he passes to Short, Long or Long Bomb
 * range.
 */
@RulesCollection(Rules.COMMON)
public class StrongArm extends Skill {

	public StrongArm() {
		super("Strong Arm", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct(ModifierDictionary dictionary) {
		registerModifier(new PassModifier("Strong Arm", -1, false, false) {
			@Override
			public boolean appliesToContext(PassContext context) {
				return context.distance != PassingDistance.QUICK_PASS;
			}
		});
	}

}
