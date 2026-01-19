package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;

public class Saboteur extends InjuryType {

	public Saboteur() {
		super("saboteur", false, SendToBoxReason.SABOTEUR);
	}

	@Override
	public boolean isCausedByOpponent() {
		return false;
	}

	@Override
	public boolean canUseApo() {
		return false;
	}

	@Override
	public boolean fallingDownCausesTurnover() {
		return false;
	}
}

