package com.fumbbl.ffb.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportLookIntoMyEyesRoll extends ReportSkillRoll {

	public ReportLookIntoMyEyesRoll() {
	}

	public ReportLookIntoMyEyesRoll(String pPlayerId, boolean pSuccessful, int pRoll,
																	boolean pReRolled) {
		super(pPlayerId, pSuccessful, pRoll, 2, pReRolled, new RollModifier[0]);
	}

	@Override
	public ReportId getId() {
		return ReportId.LOOK_INTO_MY_EYES_ROLL;
	}

	@Override
	public ReportLookIntoMyEyesRoll transform(IFactorySource source) {
		return new ReportLookIntoMyEyesRoll(getPlayerId(), isSuccessful(), getRoll(), isReRolled());
	}

}
