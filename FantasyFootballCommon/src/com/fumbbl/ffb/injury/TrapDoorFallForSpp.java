package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Player;

public class TrapDoorFallForSpp extends InjuryType {

	public TrapDoorFallForSpp() {
		super("trapdoorFallForSpp", true, SendToBoxReason.TRAP_DOOR_FALL);
	}

	@Override
	public boolean canApoKoIntoStun() {
		return false;
	}

	@Override
	public boolean fallingDownCausesTurnover() {
		return false;
	}

	@Override
	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender) {
		string.append(defender.getName());
		string.append("  is injured by the fall.");
	}

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}
}
