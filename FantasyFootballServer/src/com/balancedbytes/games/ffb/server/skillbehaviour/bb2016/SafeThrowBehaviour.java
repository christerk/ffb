package com.balancedbytes.games.ffb.server.skillbehaviour.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.bb2016.pass.StepSafeThrow;
import com.balancedbytes.games.ffb.skill.SafeThrow;

@RulesCollection(Rules.BB2016)
public class SafeThrowBehaviour extends SkillBehaviour<SafeThrow> {
	public SafeThrowBehaviour() {
		super();

		registerStep(StepId.SAFE_THROW, StepSafeThrow.class);
	}
}
