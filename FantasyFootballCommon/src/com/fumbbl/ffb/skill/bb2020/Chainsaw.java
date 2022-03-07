package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;

/**
 * A player armed with a chainsaw must attack with it instead of making a block
 * as part of a Block or Blitz Action. When the chainsaw is used to make an
 * attack, roll a D6 instead of the Block dice. On a roll of 2 or more the
 * chainsaw hits the opposing player, but on a roll of 1 it kicks back and hits
 * the wielder instead! Make an Armour roll for the player hit by the chainsaw,
 * adding 3 to the score. If the roll beats the victim's Armour value then the
 * victim is Knocked Down and injured - roll on the Injury table. If the roll
 * fails to beat the victim's Armour value then the attack has no effect. A
 * player armed with a chainsaw may take a Foul Action, and adds 3 to the Armour
 * roll, but must roll for kick back as described above. A running chainsaw is a
 * dangerous thing to carry around, and so if a player holding a chainsaw is
 * Knocked Down for any reason, the opposing coach is allowed to add 3 to his
 * Armour roll to see if the player was injured. However blocking a player with
 * a chainsaw is equally dangerous, if an opponent knocks himself over when
 * blocking the chainsaw player then add 3 to his Armour roll. This skill may
 * only be used once per turn (i.e. cannot be used with Frenzy or Multiple
 * Block) and if used as part of a Blitz Action, the player cannot continue
 * moving after using it. Casualties caused by a chainsaw player do not count
 * for Star Player points.
 */
@RulesCollection(Rules.BB2020)
public class Chainsaw extends Skill {

	public Chainsaw() {
		super("Chainsaw", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.blocksLikeChainsaw);
		registerProperty(NamedProperties.providesBlockAlternative);
		registerProperty(NamedProperties.providesChainsawBlockAlternative);
		registerProperty(NamedProperties.providesChainsawFoulingAlternative);
		registerProperty(NamedProperties.providesFoulingAlternative);
		
		registerProperty(NamedProperties.preventStuntyDodgeModifier);
		registerProperty(new CancelSkillProperty(NamedProperties.ignoreTacklezonesWhenDodging));

		registerModifier(new StaticArmourModifier("Chainsaw", 3, false, true) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return false;
			}
		});
	}

}
