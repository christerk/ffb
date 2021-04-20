package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;

/**
 * The player may add 1 to the D6 when attempting a TTM action.
 */
@RulesCollection(Rules.BB2020)
public class StrongArm extends Skill {

	public StrongArm() {
		super("Strong Arm", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerModifier(new PassModifier("Strong Arm", -1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, PassContext context) {
				return context.isDuringThrowTeamMate();
			}
		});
	}

}
