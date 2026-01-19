package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * When this player is Knocked Down as a result of an opposition player’s 
 * Block Action, before the Armour Roll is made, they may roll a D6. 
 * On a 1–3, nothing happens and the Armour Roll is made as normal. 
 * On a 4+, this player’s sabotaged weapon goes off and the opposition 
 * player is also Knocked Down, though this will not cause a Turnover 
 * unless the opposition player was holding the ball. If this player’s 
 * sabotaged weapon goes off, then they are automatically Knocked Out and 
 * the Armour Roll is not made for them.
 * A player without the Secret Weapon Trait cannot have this Skill.
 */

@RulesCollection(RulesCollection.Rules.BB2025)
public class Saboteur extends Skill {

	public Saboteur() {
		super("Saboteur", SkillCategory.DEVIOUS);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canSabotageBlockerOnKnockdown);
	}

	@Override
	public boolean canBeAssignedTo(Player<?> player) {
		return player.hasSkillProperty(NamedProperties.getsSentOffAtEndOfDrive);
	}
}