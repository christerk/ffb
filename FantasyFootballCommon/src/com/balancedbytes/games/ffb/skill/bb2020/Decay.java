package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.modifiers.bb2020.CasualtyModifier;

/**
 * Staying on the pitch is difficult when your rotting body is barely held
 * together. When this player suffers a Casualty result on the Injury table,
 * roll twice on the Casualty table (see page 25) and apply both results. The
 * player will only ever miss one future match as a result of his injuries, even
 * if he suffers two results with this effect.
 */
@RulesCollection(Rules.BB2020)
public class Decay extends Skill {

	public Decay() {
		super("Decay", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerModifier(new CasualtyModifier("Decay", 1));
	}

}
