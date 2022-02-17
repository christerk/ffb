package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.SavageMaulingModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2020.special.SavageMauling;

@RulesCollection(RulesCollection.Rules.BB2020)
public class SavageMaulingBehaviour extends SkillBehaviour<SavageMauling> {

	public SavageMaulingBehaviour() {
		super();

		registerModifier(new SavageMaulingModification());
	}
}
