package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;

@RulesCollection(Rules.BB2025)
public class BallAndChain extends com.fumbbl.ffb.skill.bb2020.BallAndChain {

	@Override
	public void postConstruct() {
		super.postConstruct();
		// TODO BB25: add Eye Gouge conflict once the skill is implemented in this ruleset.
		registerConflictingProperty(NamedProperties.canMoveWhenOpponentPasses);
		registerConflictingProperty(NamedProperties.canPushBackToAnySquare);
	}
}

