package com.fumbbl.ffb;

import com.fumbbl.ffb.model.Player;

/**
 * @author Kalimar
 */
public abstract class InjuryType implements INamedObject {

	private final String name;
	private final boolean worthSpps;
	private SendToBoxReason sendToBoxReason = null;
	private boolean failedArmourPlacesProne = true;

	protected InjuryContext injuryContext;

	protected InjuryType(String pName, boolean pWorthSpps, SendToBoxReason pSendToBoxReason) {
		name = pName;
		worthSpps = pWorthSpps;
		sendToBoxReason = pSendToBoxReason;
		injuryContext = new InjuryContext();
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

	public boolean isVomit() {
		return false;
	}

	public InjuryContext injuryContext() {
		return this.injuryContext;
	}

	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender) {

	}

	public void setFailedArmourPlacesProne(boolean flag) {
		this.failedArmourPlacesProne = flag;
	}

}
