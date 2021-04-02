package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.modifiers.RollModifier;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportFoulAppearanceRoll extends ReportSkillRoll {

	public ReportFoulAppearanceRoll() {
	}

	public ReportFoulAppearanceRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                                boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.FOUL_APPEARANCE_ROLL;
	}

	@Override
	public ReportFoulAppearanceRoll transform(IFactorySource source) {
		return new ReportFoulAppearanceRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
