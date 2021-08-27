package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.InjuryType;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Player;

public class CrowdPushForSpp extends InjuryType {

	public CrowdPushForSpp() {
		super("crowdpushForSpp", true, SendToBoxReason.CROWD_PUSHED);
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

	@Override
	public boolean isCausedByOpponent() {
		return true;
	}

}
