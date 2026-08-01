package com.fumbbl.ffb.server.model;

/**
 * Tracks the turnover state caused (or avoided) by a single player, so steps
 * collecting turnovers for multiple players can keep them apart.
 */
public class TurnoverUpdate {
	private final String playerId;
	private final boolean turnover;

	public TurnoverUpdate(String playerId, boolean turnover) {
		this.playerId = playerId;
		this.turnover = turnover;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isTurnover() {
		return turnover;
	}
}
