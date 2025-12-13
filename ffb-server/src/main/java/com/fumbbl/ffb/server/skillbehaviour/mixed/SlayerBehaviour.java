package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.SlayerModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.special.Slayer;


@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class SlayerBehaviour extends SkillBehaviour<Slayer> {

	public SlayerBehaviour() {
		super();

		registerModifier(new SlayerModification());
	}
}
