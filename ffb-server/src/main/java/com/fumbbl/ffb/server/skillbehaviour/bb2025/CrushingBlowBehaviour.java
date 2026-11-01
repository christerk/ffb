package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.CrushingBlowModification;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.CrushingBlowChainModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.special.CrushingBlow;

@RulesCollection(RulesCollection.Rules.BB2025)
public class CrushingBlowBehaviour extends SkillBehaviour<CrushingBlow> {
	public CrushingBlowBehaviour() {
		super();
		registerModifier(new CrushingBlowModification());
		registerModifier(new CrushingBlowChainModification());
	}
}
