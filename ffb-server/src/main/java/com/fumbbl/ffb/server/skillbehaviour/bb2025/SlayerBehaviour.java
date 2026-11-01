package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.server.injury.modification.bb2025.SlayerModification;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.AvOrInjChainModification;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.KeywordRestriction;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.special.Slayer;


@RulesCollection(RulesCollection.Rules.BB2025)
public class SlayerBehaviour extends SkillBehaviour<Slayer> {

	public SlayerBehaviour() {
		super();

		registerModifier(new SlayerModification());
		registerModifier(new AvOrInjChainModification(new KeywordRestriction(Keyword.BIG_GUY)));
	}
}
