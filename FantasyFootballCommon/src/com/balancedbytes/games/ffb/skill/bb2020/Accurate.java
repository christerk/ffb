package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.modifiers.PassModifier;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.modifiers.PassContext;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;

import java.util.HashSet;
import java.util.Set;

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
	public void postConstruct(){
		registerModifier(new PassModifier("Accurate", -1, false, false) {
			private Set<PassingDistance> shortDistances = new HashSet<PassingDistance>() {{
				add(PassingDistance.SHORT_PASS);
				add(PassingDistance.QUICK_PASS);
			}};
			@Override
			public boolean appliesToContext(PassContext context) {
				return shortDistances.contains(context.getDistance());
			}
		});
	}

}
