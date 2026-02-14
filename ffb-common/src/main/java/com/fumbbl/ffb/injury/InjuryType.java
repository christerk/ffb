package com.fumbbl.ffb.injury;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.model.Player;

/**
 * @author Kalimar
 */
public abstract class InjuryType implements INamedObject {

	private final String name;
	private final boolean worthSpps;
	private final SendToBoxReason sendToBoxReason;
	private boolean failedArmourPlacesProne = true;

	protected InjuryType(String pName, boolean pWorthSpps, SendToBoxReason pSendToBoxReason) {
		name = pName;
		worthSpps = pWorthSpps;
		sendToBoxReason = pSendToBoxReason;
	}

	public InjuryType injuryType() {
		return this;
	}

	public String getName() {
		return name;
	}

	public boolean isWorthSpps() {
		return worthSpps;
	}

	public SendToBoxReason sendToBoxReason() {
		return sendToBoxReason;
	}

	public boolean isCausedByOpponent() {
		return false;
	}

	public boolean canUseApo() {
		return true;
	}

	public boolean canApoKoIntoStun() {
		return true;
	}

	public boolean shouldPlayFallSound() {
		return true;
	}

	public boolean fallingDownCausesTurnover() {
		return true;
	}

	public boolean failedArmourPlacesProne() {
		return failedArmourPlacesProne;
	}

	public boolean isStab() {
		return false;
	}

	public boolean isFoul() {
		return false;
	}

	public boolean isVomitLike() {
		return false;
	}

	public boolean isChainsaw() {
		return false;
	}

	public boolean isBlock() {
		return false;
	}

	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender) {

	}

	public void setFailedArmourPlacesProne(boolean flag) {
		this.failedArmourPlacesProne = flag;
	}

}
