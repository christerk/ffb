package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public abstract class InjuryType implements INamedObject {

	private String name;
	private boolean worthSpps;
	private SendToBoxReason sendToBoxReason = null;

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

	public boolean isStab() {
		return false;
	}

	public boolean isFoul() {
		return false;
	}

	public InjuryContext injuryContext() {
		return this.injuryContext;
	}

	public void reportInjuryString(StringBuilder string, Player<?> attacker, Player<?> defender) {

	}

}
