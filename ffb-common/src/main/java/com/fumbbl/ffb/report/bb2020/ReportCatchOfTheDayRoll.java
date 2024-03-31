package com.fumbbl.ffb.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportCatchOfTheDayRoll extends ReportSkillRoll {


	public ReportCatchOfTheDayRoll() {
	}

	public ReportCatchOfTheDayRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, null);
	}

	@Override
	public ReportId getId() {
		return ReportId.CATCH_OF_THE_DAY;
	}


	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportCatchOfTheDayRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled());
	}

}
