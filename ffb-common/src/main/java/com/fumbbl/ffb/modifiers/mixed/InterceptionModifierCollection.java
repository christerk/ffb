package com.fumbbl.ffb.modifiers.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.InterceptionContext;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class InterceptionModifierCollection extends com.fumbbl.ffb.modifiers.InterceptionModifierCollection {

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
		add(new InterceptionModifier("Thrower has Stunty", -1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, InterceptionContext context) {
				return context.getGame().getThrower().hasSkillProperty(NamedProperties.passesAreInterceptedEasier) && !context.isBomb();
			}
		});
	}
}
