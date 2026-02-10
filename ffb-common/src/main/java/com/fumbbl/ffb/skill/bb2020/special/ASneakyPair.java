package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.StaticInjuryModifierAttacker;
import com.fumbbl.ffb.util.UtilPlayer;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ASneakyPair extends Skill {
	public ASneakyPair() {
		super("A Sneaky Pair", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerModifier(new StaticInjuryModifierAttacker(ASneakyPair.this.getName(), 1, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return super.appliesToContext(context) && (context.isFoul() || context.isStab()) 
					&& UtilPlayer.partnerMarksDefender(context.getGame(), context.getDefender(), ASneakyPair.this);
			}
		});
	}
}
