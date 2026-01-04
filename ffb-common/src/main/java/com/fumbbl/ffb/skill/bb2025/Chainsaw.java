package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.DeclareCondition;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;

/**
 * When this player is activated, they can declare a Chainsaw Attack Special 
 * Action; there is no limit to the number of players that can declare this 
 * Special Action each Turn.
 * When a player performs a Chainsaw Attack Special Action, roll a D6. On  2+, 
 * this player may immediately make an Armour Roll against one adjacent 
 * Standing opposition player applying a +3 modifier to the Armour Roll.
 * On a 1, the Chainsaw will Kick-back and this player is Knocked Down instead.
 * If this player is Knocked Down or Falls Over for any reason, regardless of 
 * how it occurred, then a +3 modifier is applied when the opposition Coach 
 * makes an Armour Roll for this player. This +3 modifier must always be applied.
 * Should they wish, this player may also use their chainsaw when performing a 
 * Foul Action, in which case they may apply a +3 modifier when making the 
 * Armour Roll for the opposition player. They will still need to roll for 
 * Kick-back as normal.
 * This player may use the Chainsaw Attack Special Action to replace the Block 
 * Action made as part of a Blitz Action if they wish though their activation will 
 * still end a soon as they have performed the Chainsaw Attack Special Action.
 */
@RulesCollection(Rules.BB2025)
public class Chainsaw extends Skill {

	public Chainsaw() {
		super("Chainsaw", SkillCategory.TRAIT);
		setDeclareCondition(DeclareCondition.STANDING);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.blocksLikeChainsaw);
		registerProperty(NamedProperties.providesBlockAlternative);
		registerProperty(NamedProperties.providesChainsawBlockAlternative);
		registerProperty(NamedProperties.providesChainsawFoulingAlternative);
		registerProperty(NamedProperties.providesFoulingAlternative);
		
		registerModifier(new StaticArmourModifier("Chainsaw", 3, false, true) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return false;
			}
		});
	}

}
