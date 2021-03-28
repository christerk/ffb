package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.property.CancelSkillProperty;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.InjuryModifierContext;
import com.balancedbytes.games.ffb.modifiers.ModifierType;
import com.balancedbytes.games.ffb.modifiers.PassModifier;
import com.balancedbytes.games.ffb.modifiers.StaticInjuryModifier;

/**
 * The player is so small that they are very difficult to tackle because they
 * can duck underneath opposing players' outstretched arms and run between their
 * legs. On the other hand, Stunty players are just a bit too small to throw the
 * ball very well, and are easily injured. To represent these things a player
 * with the Stunty skill may ignore any enemy tackle zones on the square he is
 * moving to when he makes a Dodge roll (i.e., they always end up with a +1
 * Dodge roll modifier), but must subtract 1 from the roll when they pass. In
 * addition, this player treats a roll of 7 and 9 on the Injurytable after any
 * modifiers have been applied as a KO'd and Badly Hurt result respectively
 * rather than the normal results. Stunties that are armed with a Secret Weapon
 * are not allowed to ignore enemy tackle zones, but still suffer the other
 * penalties.
 */
@RulesCollection(Rules.COMMON)
public class Stunty extends Skill {

	public Stunty() {
		super("Stunty", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerModifier(new PassModifier("Stunty", 1, ModifierType.REGULAR));
		registerModifier(new StaticInjuryModifier("Stunty", 0, false) {
			@Override
			public boolean appliesToContext(InjuryModifierContext context) {
				boolean applies = false;

				if (!context.isStab() &&
					!context.getDefender().hasSkillProperty(NamedProperties.preventDamagingInjuryModifications) &&
					context.getDefender().hasSkillProperty(NamedProperties.isHurtMoreEasily)) {
					applies = true;
				}

				return applies;
			}
		});

		registerProperty(NamedProperties.smallIcon);
		registerProperty(NamedProperties.preventRaiseFromDead);
		registerProperty(new CancelSkillProperty(NamedProperties.hasNurglesRot));
		registerProperty(NamedProperties.ignoreTacklezonesWhenDodging);
		registerProperty(NamedProperties.isHurtMoreEasily);
	}

}
