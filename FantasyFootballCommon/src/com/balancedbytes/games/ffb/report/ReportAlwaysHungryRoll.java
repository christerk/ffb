package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportAlwaysHungryRoll extends ReportSkillRoll {

	public ReportAlwaysHungryRoll() {
	}

	public ReportAlwaysHungryRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                       boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.ALWAYS_HUNGRY_ROLL;
	}

	@Override
	public ReportAlwaysHungryRoll transform(IFactorySource source) {
		return new ReportAlwaysHungryRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
