package com.fumbbl.ffb.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportBloodLustRoll extends ReportSkillRoll {

	public ReportBloodLustRoll() {
	}

	public ReportBloodLustRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                           boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.BLOOD_LUST_ROLL;
	}

	@Override
	public ReportBloodLustRoll transform(IFactorySource source) {
		return new ReportBloodLustRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
