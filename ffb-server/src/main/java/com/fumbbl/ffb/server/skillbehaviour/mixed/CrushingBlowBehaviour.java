package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.CrushingBlowModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.special.CrushingBlow;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class CrushingBlowBehaviour extends SkillBehaviour<CrushingBlow> {
	public CrushingBlowBehaviour() {
		super();
		registerModifier(new CrushingBlowModification());
	}
}
