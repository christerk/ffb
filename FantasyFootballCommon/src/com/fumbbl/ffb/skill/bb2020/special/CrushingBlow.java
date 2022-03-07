package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;

/**
 * Once per game, when an opposition player is Knocked Down as the result of a Block action performed by Varag
 * you may apply an additional +1 modifier to the Armour Roll.
 * This Modifier may be applied after the roll has been made.
 */

@RulesCollection(Rules.BB2020)
public class CrushingBlow extends Skill {
	public CrushingBlow() {
		super("Crushing Blow", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		super.postConstruct();
		registerModifier(new StaticArmourModifier("Crushing Blow", 1, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return false;
			}
		});
	}
}
