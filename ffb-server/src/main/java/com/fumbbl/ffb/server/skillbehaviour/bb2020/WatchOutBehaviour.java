package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.skill.bb2020.special.WatchOut;

@RulesCollection(Rules.BB2020)
public class WatchOutBehaviour extends AbstractDodgingBehaviour<WatchOut> {

	public WatchOutBehaviour() {
		super(2, true);
	}
}
