package com.fumbbl.ffb.skill.bb2025;

import java.util.Arrays;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.InjuryModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;
import com.fumbbl.ffb.modifiers.StaticInjuryModifierAttacker;
import com.fumbbl.ffb.util.UtilCards;

/**
 * When this player is thrown as part of a Throw Team-mate Action, if they 
 * land in a square that contains an opposition player, including if they 
 * Bounce into a square containing an opposition player, and the opposition 
 * player is Knocked Down, then they may apply a +1 modifier to either the 
 * Armour Roll or Injury Roll. This modifier may be applied after the roll
 * has been made. If an opposition player suffers a Casualty as a result
 * of being Knocked Down by the thrown player with this Skill, then this 
 * player will count as having caused that Casualty and will receive 
 * Star Player Points as appropriate.
 * A player without the Right Stuff Trait cannot have this Skill.
 */
@RulesCollection(Rules.BB2025)
public class LethalFlight extends Skill {

	public LethalFlight() {
		super("Lethal Flight", SkillCategory.DEVIOUS);
	}

	@Override
	public void postConstruct() {
		registerModifier(new StaticArmourModifier("Lethal Flight", 1, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				return context.getAttacker() != null
					&& UtilCards.hasSkill(context.getAttacker(), registeredTo)
					&& context.getAttacker().getTeam() != context.getDefender().getTeam();
			}
		});
		registerModifier(new StaticInjuryModifierAttacker("Lethal Flight", 1, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				return super.appliesToContext(context)
					&& context.getAttacker() != null
					&& context.getAttacker().getTeam() != context.getDefender().getTeam()
					&& Arrays.stream(context.getInjuryContext().getArmorModifiers())
						.noneMatch(mod -> mod.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnTtm));
			}
		});
		registerProperty(NamedProperties.affectsEitherArmourOrInjuryOnTtm);
		registerProperty(NamedProperties.grantsSppWhenHittingOpponentOnTtm);
	}

	@Override
	public boolean canBeAssignedTo(Player<?> player) {
		return player.hasSkillProperty(NamedProperties.canBeThrown);
	}

}
