package com.fumbbl.ffb.modifiers.bb2025;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.CatchContext;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class CatchModifierCollection extends com.fumbbl.ffb.modifiers.CatchModifierCollection {

	public CatchModifierCollection() {
		super();

		add(new CatchModifier("Inaccurate Pass or Scatter", 1, ModifierType.REGULAR) {
			private final Set<CatchScatterThrowInMode> scatter = new HashSet<CatchScatterThrowInMode>() {
				private static final long serialVersionUID = 6752365907656902172L;

				{
					add(CatchScatterThrowInMode.CATCH_BOMB);
					add(CatchScatterThrowInMode.CATCH_SCATTER);
				}
			};

			@Override
			public boolean appliesToContext(Skill skill, CatchContext context) {
				return super.appliesToContext(skill, context) && scatter.contains(context.getCatchMode());
			}
		});

		add(new CatchModifier("Blast It!", -1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, CatchContext context) {
				return super.appliesToContext(skill, context) && context.getUsingBlastIt()
					&& context.getGame().getActingTeam().hasPlayer(context.getPlayer())
					&& (context.getCatchMode() == CatchScatterThrowInMode.CATCH_SCATTER
					|| context.getCatchMode() == CatchScatterThrowInMode.CATCH_MISSED_PASS);
			}
		});
	}
}
