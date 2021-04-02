package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportAnimosityRoll extends ReportSkillRoll {

	public ReportAnimosityRoll() {
	}

	public ReportAnimosityRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                           boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.ANIMOSITY_ROLL;
	}

	@Override
	public ReportAnimosityRoll transform(IFactorySource source) {
		return new ReportAnimosityRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
