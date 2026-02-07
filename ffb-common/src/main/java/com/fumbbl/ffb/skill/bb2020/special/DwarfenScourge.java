package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.VariableArmourModifier;
import com.fumbbl.ffb.modifiers.VariableInjuryModifierAttacker;

@RulesCollection(Rules.BB2020)
public class DwarfenScourge extends Skill {
	public DwarfenScourge() {
		super("Dwarfen Scourge", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerModifier(new VariableArmourModifier("DwarfenScourge", false) {
			@Override
			public int getModifier(Player<?> attacker, Player<?> defender) {
				return defender.getPosition().isDwarf() ? 2 : 1;
			}

			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return false;
			}
		});

		registerModifier(new VariableInjuryModifierAttacker("DwarfenScourge", false) {
			@Override
			public int getModifier(Player<?> attacker, Player<?> defender) {
				return defender.getPosition().isDwarf() ? 2 : 1;
			}

			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return false;
			}
		});
	}
}
