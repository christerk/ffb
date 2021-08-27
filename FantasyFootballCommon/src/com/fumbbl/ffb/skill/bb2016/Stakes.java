package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifierContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;

/**
 * This player is armed with special stakes that are blessed to cause extra
 * damage to the Undead and those that work with them. This player may add 1 to
 * the Armour roll when they make a Stab attack against any player playing for a
 * Khemri, Necromantic, Undead or Vampire team.
 */
@RulesCollection(Rules.BB2016)
public class Stakes extends Skill {

	public Stakes() {
		super("Stakes", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerModifier(new StaticArmourModifier("Stakes", 1, false) {
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
		registerProperty(NamedProperties.providesBlockAlternative);
	}

}
