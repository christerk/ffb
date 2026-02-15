package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.bb2025.ToxinConnoisseurModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.special.ToxinConnoisseur;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ToxinConnoisseurBehaviour extends SkillBehaviour<ToxinConnoisseur> {

	public ToxinConnoisseurBehaviour() {
		super();

		registerModifier(new ToxinConnoisseurModification());
	}
}
