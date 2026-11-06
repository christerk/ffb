package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.ClawsChainModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.Claws;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ClawsBehaviour extends SkillBehaviour<Claws> {

	public ClawsBehaviour() {
		super();
		registerModifier(new ClawsChainModification());
	}
}
