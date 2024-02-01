package com.fumbbl.ffb.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportRightStuffRoll extends ReportSkillRoll {

	public ReportRightStuffRoll() {
	}

	public ReportRightStuffRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                            boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.RIGHT_STUFF_ROLL;
	}

	@Override
	public ReportRightStuffRoll transform(IFactorySource source) {
		return new ReportRightStuffRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
