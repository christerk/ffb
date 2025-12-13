package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.AvOrInjModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.special.DwarfenScourge;

@RulesCollection(RulesCollection.Rules.BB2020)
public class DwarfenScourgeBehaviour extends SkillBehaviour<DwarfenScourge> {

	public DwarfenScourgeBehaviour() {
		super();

		registerModifier(new AvOrInjModification());
	}
}
