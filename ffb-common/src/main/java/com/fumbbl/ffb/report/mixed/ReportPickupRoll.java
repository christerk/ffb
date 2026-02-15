package com.fumbbl.ffb.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportPickupRoll extends com.fumbbl.ffb.report.ReportPickupRoll {

	public ReportPickupRoll() {
	}

	public ReportPickupRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                        boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportPickupRoll transform(IFactorySource source) {
		return new ReportPickupRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
