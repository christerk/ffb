package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.skill.mixed.special.WatchOut;

@RulesCollection(Rules.BB2025)
public class WatchOutBehaviour extends AbstractDodgingBehaviour<WatchOut> {

	public WatchOutBehaviour() {
		super(2, true);
	}
}
