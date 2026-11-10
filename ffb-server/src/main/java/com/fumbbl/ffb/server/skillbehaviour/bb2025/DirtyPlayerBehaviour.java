package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.DirtyPlayerChainModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2025.DirtyPlayer;

@RulesCollection(RulesCollection.Rules.BB2025)
public class DirtyPlayerBehaviour extends SkillBehaviour<DirtyPlayer> {

	public DirtyPlayerBehaviour() {
		super();
		registerModifier(new DirtyPlayerChainModification());
	}
}
