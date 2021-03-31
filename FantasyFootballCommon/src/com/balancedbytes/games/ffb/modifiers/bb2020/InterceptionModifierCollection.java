package com.balancedbytes.games.ffb.modifiers.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.modifiers.InterceptionContext;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2020)
public class InterceptionModifierCollection extends com.balancedbytes.games.ffb.modifiers.InterceptionModifierCollection {

	public InterceptionModifierCollection() {
		super();
		add(new InterceptionModifier("Accurate Pass", 3, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, InterceptionContext context) {
				return super.appliesToContext(skill, context) && context.getPassResult() == PassResult.ACCURATE;
			}
		});
		add(new InterceptionModifier("Inaccurate Pass", 2, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, InterceptionContext context) {
				return super.appliesToContext(skill, context) && context.getPassResult() == PassResult.INACCURATE;
			}
		});
		add(new InterceptionModifier("Wildly Inaccurate Pass", 1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, InterceptionContext context) {
				return super.appliesToContext(skill, context) && context.getPassResult() == PassResult.WILDLY_INACCURATE;
			}
		});
		add(new InterceptionModifier("1 Tacklezone", "1 for being marked", 1, 1, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("2 Tacklezone", "1 for being marked", 1, 2, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("3 Tacklezone", "1 for being marked", 1, 3, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("4 Tacklezone", "1 for being marked", 1, 4, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("5 Tacklezone", "1 for being marked", 1, 5, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("6 Tacklezone", "1 for being marked", 1, 6, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("7 Tacklezone", "1 for being marked", 1, 7, ModifierType.TACKLEZONE));
		add(new InterceptionModifier("8 Tacklezone", "1 for being marked", 1, 8, ModifierType.TACKLEZONE));
	}
}
