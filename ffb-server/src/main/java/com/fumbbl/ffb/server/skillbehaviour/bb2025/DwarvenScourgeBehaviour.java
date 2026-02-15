package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.AvOrInjModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2025.special.DwarvenScourge;

@RulesCollection(RulesCollection.Rules.BB2025)
public class DwarvenScourgeBehaviour extends SkillBehaviour<DwarvenScourge> {

	public DwarvenScourgeBehaviour() {
		super();

		registerModifier(new AvOrInjModification());
	}
}
