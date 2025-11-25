package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.skill.common.Dodge;

@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class DodgeBehaviour extends AbstractDodgingBehaviour<Dodge> {

	public DodgeBehaviour() {
		super(1, false);
	}

}
