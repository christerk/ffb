package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.ArmBarChainModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.ArmBar;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ArmBarBehaviour extends SkillBehaviour<ArmBar> {

	public ArmBarBehaviour() {
		super();
		registerModifier(new ArmBarChainModification());
	}
}
