package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.AvOrInjModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2020.special.DwarfenScourge;

@RulesCollection(RulesCollection.Rules.BB2020)
public class DwarfenScourgeBehaviour extends SkillBehaviour<DwarfenScourge> {

	public DwarfenScourgeBehaviour() {
		super();

		registerModifier(new AvOrInjModification());
	}
}
