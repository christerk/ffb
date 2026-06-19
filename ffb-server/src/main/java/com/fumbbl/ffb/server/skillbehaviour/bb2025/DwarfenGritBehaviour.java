package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.bb2025.DwarfenGritModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2025.special.DwarfenGrit;

@RulesCollection(RulesCollection.Rules.BB2025)
public class DwarfenGritBehaviour extends SkillBehaviour<DwarfenGrit> {

	public DwarfenGritBehaviour() {
		super();
		registerModifier(new DwarfenGritModification());
	}
}
