package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportEscapeRoll extends ReportSkillRoll {

	public ReportEscapeRoll() {
	}

	public ReportEscapeRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                        boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.ESCAPE_ROLL;
	}

	@Override
	public ReportEscapeRoll transform(IFactorySource source) {
		return new ReportEscapeRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
