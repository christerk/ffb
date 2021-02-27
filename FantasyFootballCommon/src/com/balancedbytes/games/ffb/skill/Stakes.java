package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.modifiers.ArmorModifier;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.modifiers.ArmorModifierContext;

/**
 * This player is armed with special stakes that are blessed to cause extra
 * damage to the Undead and those that work with them. This player may add 1 to
 * the Armour roll when they make a Stab attack against any player playing for a
 * Khemri, Necromantic, Undead or Vampire team.
 */
@RulesCollection(Rules.COMMON)
public class Stakes extends Skill {

	public Stakes() {
		super("Stakes", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerModifier(new ArmorModifier("Stakes", 1, false) {
			@Override
			public boolean appliesToContext(ArmorModifierContext context) {
				boolean applies = false;

				Team otherTeam = context.getGame().getTeamHome().hasPlayer(context.getDefender()) ? context.getGame().getTeamHome()
					: context.getGame().getTeamAway();
				if (context.isStab() && (context.getAttacker() != null) && (otherTeam.getRoster().isUndead()
					|| ((context.getDefender() != null) && context.getDefender().getPosition().isUndead()))) {
					applies = true;
				}
				return applies;
			}
		});
		registerProperty(NamedProperties.canPerformArmourRollInsteadOfBlock);
		registerProperty(NamedProperties.armourRollWithoutBlockHasIncreasedEffectOnUndead);
	}

}
