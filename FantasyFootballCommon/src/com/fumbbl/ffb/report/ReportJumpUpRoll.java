package com.fumbbl.ffb.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportJumpUpRoll extends ReportSkillRoll {

	public ReportJumpUpRoll() {
	}

	public ReportJumpUpRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                        boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.JUMP_UP_ROLL;
	}

	@Override
	public ReportJumpUpRoll transform(IFactorySource source) {
		return new ReportJumpUpRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
