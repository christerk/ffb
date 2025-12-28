package com.fumbbl.ffb.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportPickupRoll extends ReportSkillRoll {

	@SuppressWarnings("unused")
	public ReportPickupRoll() {
	}

	public ReportPickupRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
													boolean pReRolled, RollModifier<?>[] pRollModifiers) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
	}

	@Override
	public ReportId getId() {
		return ReportId.PICK_UP_ROLL;
	}

	@Override
	public ReportPickupRoll transform(IFactorySource source) {
		return new ReportPickupRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers());
	}
}
