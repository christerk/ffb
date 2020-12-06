package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.modifier.CancelSkillProperty;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;

/**
 * If the player suffers a Casualty result on the Injury table, then roll a D6
 * for Regeneration after the roll on the Casualty table and after any
 * Apothecary roll if allowed. On a result of 1-3, the player suffers the result
 * of this injury. On a 4-6, the player will heal the injury after a short
 * period of time to 're-organise' himself, and is placed in the Reserves box
 * instead. Regeneration rolls may not be re-rolled. Note that opposing players
 * still earn Star Player points as normal for inflicting a Casualty result on a
 * player with this skill, even if the result doesn't affect the player in the
 * normal way.
 */
public class Regeneration extends Skill {

	public Regeneration() {
		super("Regeneration", SkillCategory.EXTRAORDINARY);

		registerProperty(NamedProperties.preventRaiseFromDead);
		registerProperty(NamedProperties.canRollToSaveFromInjury);

		registerProperty(new CancelSkillProperty(SkillConstants.NURGLES_ROT));

	}

}
