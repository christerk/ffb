package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.bb2016.pass.StepSafeThrow;
import com.fumbbl.ffb.skill.bb2016.SafeThrow;

@RulesCollection(Rules.BB2016)
public class SafeThrowBehaviour extends SkillBehaviour<SafeThrow> {
	public SafeThrowBehaviour() {
		super();

		registerStep(StepId.SAFE_THROW, StepSafeThrow.class);
	}
}
