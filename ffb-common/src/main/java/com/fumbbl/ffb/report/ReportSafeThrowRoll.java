package com.fumbbl.ffb.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportSafeThrowRoll extends ReportSkillRoll {

	public ReportSafeThrowRoll() {
	}

	public ReportSafeThrowRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                           boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.SAFE_THROW_ROLL;
	}

	@Override
	public ReportSafeThrowRoll transform(IFactorySource source) {
		return new ReportSafeThrowRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
