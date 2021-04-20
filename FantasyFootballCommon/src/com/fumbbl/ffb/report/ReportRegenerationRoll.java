package com.fumbbl.ffb.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportRegenerationRoll extends ReportSkillRoll {

	public ReportRegenerationRoll() {
	}

	public ReportRegenerationRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                              boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.REGENERATION_ROLL;
	}

	@Override
	public ReportRegenerationRoll transform(IFactorySource source) {
		return new ReportRegenerationRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
