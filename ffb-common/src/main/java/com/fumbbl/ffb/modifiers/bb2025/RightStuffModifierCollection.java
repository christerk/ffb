package com.fumbbl.ffb.modifiers.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.RightStuffContext;
import com.fumbbl.ffb.modifiers.RightStuffModifier;

@RulesCollection(RulesCollection.Rules.BB2025)
public class RightStuffModifierCollection extends com.fumbbl.ffb.modifiers.RightStuffModifierCollection {
	public RightStuffModifierCollection() {

		add(new RightStuffModifier("Subpar Throw", 1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, RightStuffContext context) {
				return context.getPassResult() == PassResult.INACCURATE;
			}
		});
		add(new RightStuffModifier("Fumbled Throw", 1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, RightStuffContext context) {
				return context.getPassResult() == PassResult.FUMBLE;
			}
		});
		add(new RightStuffModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		add(new RightStuffModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		add(new RightStuffModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE));
		add(new RightStuffModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE));
		add(new RightStuffModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE));
		add(new RightStuffModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE));
		add(new RightStuffModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE));
		add(new RightStuffModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE));
	}
}
