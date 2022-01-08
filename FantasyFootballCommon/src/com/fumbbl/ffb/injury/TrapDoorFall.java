package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Player;

public class TrapDoorFall extends InjuryType {

	public TrapDoorFall() {
		super("trapdoorFall", false, SendToBoxReason.TRAP_DOOR_FALL);
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
		string.append(" is injured by the fall.");
	}

}
