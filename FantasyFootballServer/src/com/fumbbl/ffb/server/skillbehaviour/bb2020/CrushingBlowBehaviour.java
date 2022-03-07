package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.CrushingBlowModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2020.special.CrushingBlow;

@RulesCollection(RulesCollection.Rules.BB2020)
public class CrushingBlowBehaviour extends SkillBehaviour<CrushingBlow> {
	public CrushingBlowBehaviour() {
		super();
		registerModifier(new CrushingBlowModification());
	}
}
