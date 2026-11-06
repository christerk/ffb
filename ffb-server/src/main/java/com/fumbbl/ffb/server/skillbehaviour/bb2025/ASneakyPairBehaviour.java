package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.SneakyPairChainModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2025.special.ASneakyPair;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ASneakyPairBehaviour extends SkillBehaviour<ASneakyPair> {

	public ASneakyPairBehaviour() {
		super();
		registerModifier(new SneakyPairChainModification());
	}
}
