package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.injury.modification.OldProModification;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2020.special.OldPro;

@RulesCollection(RulesCollection.Rules.BB2020)
public class OldProBehaviour extends SkillBehaviour<OldPro> {

	public OldProBehaviour() {
		super();

		registerModifier(new OldProModification());
	}
}
