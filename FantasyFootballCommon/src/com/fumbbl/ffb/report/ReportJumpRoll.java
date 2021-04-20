package com.fumbbl.ffb.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportJumpRoll extends ReportSkillRoll {

	public ReportJumpRoll() {
	}

	public ReportJumpRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                      boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.JUMP_ROLL;
	}

	@Override
	public ReportJumpRoll transform(IFactorySource source) {
		return new ReportJumpRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
