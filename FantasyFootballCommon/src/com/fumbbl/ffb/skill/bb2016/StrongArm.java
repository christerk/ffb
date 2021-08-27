package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;

/**
 * The player may add 1 to the D6 when he passes to Short, Long or Long Bomb
 * range.
 */
@RulesCollection(Rules.BB2016)
public class StrongArm extends Skill {

	public StrongArm() {
		super("Strong Arm", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerModifier(new PassModifier("Strong Arm", -1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, PassContext context) {
				return context.getDistance() != PassingDistance.QUICK_PASS;
			}
		});
	}

}
