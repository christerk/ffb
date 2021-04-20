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
		registerModifier(new PassModifier("Accurate", -1, ModifierType.REGULAR) {
			private final Set<PassingDistance> shortDistances = new HashSet<PassingDistance>() {{
				add(PassingDistance.SHORT_PASS);
				add(PassingDistance.QUICK_PASS);
			}};
			@Override
			public boolean appliesToContext(Skill skill, PassContext context) {
				return shortDistances.contains(context.getDistance());
			}
		});
	}

}
