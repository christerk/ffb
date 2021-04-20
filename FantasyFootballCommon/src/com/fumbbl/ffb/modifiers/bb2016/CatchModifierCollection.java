package com.fumbbl.ffb.modifiers.bb2016;

import java.util.HashSet;
import java.util.Set;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.CatchContext;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2016)
public class CatchModifierCollection extends com.fumbbl.ffb.modifiers.CatchModifierCollection {

	public CatchModifierCollection() {
		super();

		add(new CatchModifier("Accurate Pass", -1, ModifierType.REGULAR) {
			private final Set<CatchScatterThrowInMode> accurate = new HashSet<CatchScatterThrowInMode>() {
				private static final long serialVersionUID = -9147805080834490230L;
				{
					add(CatchScatterThrowInMode.CATCH_ACCURATE_BOMB);
					add(CatchScatterThrowInMode.CATCH_ACCURATE_PASS);
				}
			};

			private final Set<CatchScatterThrowInMode> accurateAdjacent = new HashSet<CatchScatterThrowInMode>() {
				private static final long serialVersionUID = 3746699772690521692L;
				{
					add(CatchScatterThrowInMode.CATCH_ACCURATE_BOMB_EMPTY_SQUARE);
					add(CatchScatterThrowInMode.CATCH_ACCURATE_PASS_EMPTY_SQUARE);
				}
			};

			@Override
			public boolean appliesToContext(Skill skill, CatchContext context) {
				return
					super.appliesToContext(skill, context) &&
						(accurate.contains(context.getCatchMode()) ||
							(context.getPlayer() != null && context.getPlayer().hasSkillProperty(NamedProperties.addBonusForAccuratePass) &&
								accurateAdjacent.contains(context.getCatchMode())));
			}
		});
		add(new CatchModifier("Hand Off", -1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, CatchContext context) {
				return super.appliesToContext(skill, context) && CatchScatterThrowInMode.CATCH_HAND_OFF == context.getCatchMode();
			}
		});

	}
}
