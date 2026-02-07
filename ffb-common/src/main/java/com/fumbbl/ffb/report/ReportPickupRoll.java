package com.fumbbl.ffb.report;

import com.fumbbl.ffb.modifiers.RollModifier;

public abstract class ReportPickupRoll extends ReportSkillRoll {

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

}
