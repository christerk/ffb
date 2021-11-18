package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.skill.bb2020.special.TheBallista;

@RulesCollection(Rules.BB2020)
public class TheBallistaBehaviour extends AbstractPassBehaviour<TheBallista> {
	@Override
	protected ReRollSource getReRollSource() {
		return ReRollSources.THE_BALLISTA;
	}
}