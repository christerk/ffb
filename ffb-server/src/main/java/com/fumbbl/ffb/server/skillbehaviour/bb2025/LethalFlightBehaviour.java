package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.LethalFlightChainModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2025.LethalFlight;

@RulesCollection(RulesCollection.Rules.BB2025)
public class LethalFlightBehaviour extends SkillBehaviour<LethalFlight> {

	public LethalFlightBehaviour() {
		super();
		registerModifier(new LethalFlightChainModification());
	}
}
