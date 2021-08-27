package com.fumbbl.ffb.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportWeepingDaggerRoll extends ReportSkillRoll {

	public ReportWeepingDaggerRoll() {
	}

	public ReportWeepingDaggerRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                               boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.WEEPING_DAGGER_ROLL;
	}

	@Override
	public ReportWeepingDaggerRoll transform(IFactorySource source) {
		return new ReportWeepingDaggerRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
