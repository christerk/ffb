package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.modifiers.RollModifier;

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
