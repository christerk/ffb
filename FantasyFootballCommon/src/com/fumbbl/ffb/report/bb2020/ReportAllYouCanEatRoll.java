package com.fumbbl.ffb.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportAllYouCanEatRoll extends ReportSkillRoll {

	public ReportAllYouCanEatRoll() {
	}

	public ReportAllYouCanEatRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
																boolean pReRolled) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, new RollModifier[0]);
	}

	@Override
	public ReportId getId() {
		return ReportId.ALL_YOU_CAN_EAT;
	}

	@Override
	public ReportAllYouCanEatRoll transform(IFactorySource source) {
		return new ReportAllYouCanEatRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled());
	}
}
