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
 * When this player performs a Long Pass action or a Long Bomb Pass action,
 * you may apply an additional +1 modifier to the Passing Ability test.
 */
@RulesCollection(Rules.BB2020)
public class Cannoneer extends Skill {

	public Cannoneer() {
		super("Cannoneer", SkillCategory.PASSING);
	}

	@Override
	public void postConstruct(){
		registerModifier(new PassModifier("Cannoneer", -1, false, false) {
			Set<PassingDistance> longDistances = new HashSet<PassingDistance>() {{
				add(PassingDistance.LONG_PASS);
				add(PassingDistance.LONG_BOMB);
			}};
			@Override
			public boolean appliesToContext(PassContext context) {
				return longDistances.contains(context.getDistance());
			}
		});
	}
}
