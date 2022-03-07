package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.BrutalBlockModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2020.special.BrutalBlock;

@RulesCollection(RulesCollection.Rules.BB2020)
public class BrutalBlockBehaviour extends SkillBehaviour<BrutalBlock> {

	public BrutalBlockBehaviour() {
		super();

		registerModifier(new BrutalBlockModification());
	}
}
