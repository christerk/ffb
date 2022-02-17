package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.GhostlyFlamesModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2020.special.GhostlyFlames;

@RulesCollection(RulesCollection.Rules.BB2020)
public class GhostlyFlamesBehaviour extends SkillBehaviour<GhostlyFlames> {

	public GhostlyFlamesBehaviour() {
		super();

		registerModifier(new GhostlyFlamesModification());
	}
}
