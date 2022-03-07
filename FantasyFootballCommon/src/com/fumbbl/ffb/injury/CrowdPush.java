package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Player;

public class CrowdPush extends InjuryType {

	public CrowdPush() {
		super("crowdpush", false, SendToBoxReason.CROWD_PUSHED);
		// TODO Auto-generated constructor stub
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
		string.append("  is pushed into the crowd.");
	}

}
