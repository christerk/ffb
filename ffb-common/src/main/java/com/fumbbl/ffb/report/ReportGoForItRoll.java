package com.fumbbl.ffb.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportGoForItRoll extends ReportSkillRoll {

	public ReportGoForItRoll() {
	}

	public ReportGoForItRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                         boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.GO_FOR_IT_ROLL;
	}

	@Override
	public ReportGoForItRoll transform(IFactorySource source) {
		return new ReportGoForItRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
