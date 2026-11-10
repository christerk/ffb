package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.OldProModification;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.OldProChainModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.mixed.special.OldPro;

@RulesCollection(RulesCollection.Rules.BB2025)
public class OldProBehaviour extends SkillBehaviour<OldPro> {

	public OldProBehaviour() {
		super();

		registerModifier(new OldProModification());
		registerModifier(new OldProChainModification());
	}
}
