package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.modifiers.ArmorModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.ArmorModifierContext;

/**
 * A player with this skill is blessed with a huge crab like claw or razor sharp
 * talons that make armour useless. When an opponent is Knocked Down by this
 * player during a block, any Armour roll of 8 or more after modifications
 * automatically breaks armour.
 */
@RulesCollection(Rules.COMMON)
public class Claw extends Skill {

	public Claw() {
		super("Claw", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerModifier(new ArmorModifier("Claws", 0, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				if (context.isStab() || context.isFoul() || context.getAttacker().hasSkillWithProperty(NamedProperties.blocksLikeChainsaw)) {
					return false;
				}
				return context.getDefender().getArmour() > 7;
			}
		});
		registerProperty(NamedProperties.reducesArmourToFixedValue);
	}

}
