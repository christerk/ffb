package com.fumbbl.ffb.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportSteadyFootingRoll extends ReportSkillRoll {

	public ReportSteadyFootingRoll() {
	}

	public ReportSteadyFootingRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, null);
	}

	@Override
	public ReportId getId() {
		return ReportId.STEADY_FOOTING_ROLL;
	}


	@Override
	public ReportSteadyFootingRoll transform(IFactorySource source) {
		return new ReportSteadyFootingRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled());
	}
}
