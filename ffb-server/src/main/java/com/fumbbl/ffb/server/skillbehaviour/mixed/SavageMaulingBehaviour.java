package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.SavageMaulingModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.special.SavageMauling;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class SavageMaulingBehaviour extends SkillBehaviour<SavageMauling> {

	public SavageMaulingBehaviour() {
		super();

		registerModifier(new SavageMaulingModification());
	}
}
