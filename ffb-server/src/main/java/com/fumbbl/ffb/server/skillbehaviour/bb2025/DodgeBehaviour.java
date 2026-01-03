package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.skillbehaviour.mixed.AbstractDodgingBehaviour;
import com.fumbbl.ffb.skill.bb2025.Dodge;

@RulesCollection(Rules.BB2025)
public class DodgeBehaviour extends AbstractDodgingBehaviour<Dodge> {

	public DodgeBehaviour() {
		super(1, false);
	}

}
