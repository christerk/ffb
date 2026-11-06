package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.SavageMaulingModification;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.SavageMaulingChainModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.special.SavageMauling;

@RulesCollection(RulesCollection.Rules.BB2025)
public class SavageMaulingBehaviour extends SkillBehaviour<SavageMauling> {

	public SavageMaulingBehaviour() {
		super();

		registerModifier(new SavageMaulingModification());
		registerModifier(new SavageMaulingChainModification());
	}
}
