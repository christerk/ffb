package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.modifiers.RollModifier;

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
