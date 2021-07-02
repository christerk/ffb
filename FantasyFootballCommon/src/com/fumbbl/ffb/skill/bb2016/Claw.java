package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;

/**
 * A player with this skill is blessed with a huge crab like claw or razor sharp
 * talons that make armour useless. When an opponent is Knocked Down by this
 * player during a block, any Armour roll of 8 or more after modifications
 * automatically breaks armour.
 */
@RulesCollection(Rules.BB2016)
public class Claw extends Skill {

	public Claw() {
		super("Claw", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerModifier(new StaticArmourModifier("Claws", 0, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				if (context.isStab() || context.isFoul() || context.getAttacker().hasSkillProperty(NamedProperties.blocksLikeChainsaw)) {
					return false;
				}
				return context.getDefender().getArmourWithModifiers() > 7;
			}
		});
		registerProperty(NamedProperties.reducesArmourToFixedValue);
	}

}
