package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.StaticInjuryModifierAttacker;

@RulesCollection(Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ToxinConnoisseur extends Skill {
	public ToxinConnoisseur() {
		super("Toxin Connoisseur", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerModifier(new StaticInjuryModifierAttacker("Toxin Connoisseur", 1, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return false;
			}
		});
	}
}
