package com.fumbbl.ffb.skill.bb2020;

import java.util.HashSet;
import java.util.Set;

import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;

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
		registerModifier(new PassModifier("Cannoneer", -1, ModifierType.REGULAR) {
			final Set<PassingDistance> longDistances = new HashSet<PassingDistance>() {{
				add(PassingDistance.LONG_PASS);
				add(PassingDistance.LONG_BOMB);
			}};
			@Override
			public boolean appliesToContext(Skill skill, PassContext context) {
				return longDistances.contains(context.getDistance());
			}
		});
	}
}
