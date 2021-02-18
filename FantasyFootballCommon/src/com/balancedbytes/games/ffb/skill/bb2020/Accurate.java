package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.PassingModifiers.PassContext;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.ModifierDictionary;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * When this player performs a Quick Pass action or a Short Pass action,
 * you may apply an additional +1 modifier to the Passing Ability test.
 */
@RulesCollection(Rules.BB2020)
public class Accurate extends Skill {

	public Accurate() {
		super("Accurate", SkillCategory.PASSING);
	}

	@Override
	public void postConstruct(ModifierDictionary dictionary){
		registerModifier(new PassModifier("Accurate", -1, false, false) {
			@Override
			public boolean appliesToContext(PassContext context) {
				return context.distance == PassingDistance.QUICK_PASS ||
						context.distance == PassingDistance.SHORT_PASS;
			}
		});
	}

}
