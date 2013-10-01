package com.balancedbytes.games.ffb.server.step.action.common;

/**
 * 
 * @author Kalimar
 */
public enum ApothecaryMode {
	
	ATTACKER(1),
	AWAY(2),
	CROWD_PUSH(3),
	DEFENDER(4),
	FEEDING(5),
	HOME(6),
	SPECIAL_EFFECT(7),
	THROWN_PLAYER(8),
	HIT_PLAYER(9),
	CATCHER(10);
	
	private int fId;
	
	private ApothecaryMode(int pId) {
		fId = pId;
	}
	
	public int getId() {
		return fId;
	}
	
	public static ApothecaryMode fromId(int pId) {
		for (ApothecaryMode stepLabel : values()) {
			if (stepLabel.getId() == pId) {
				return stepLabel;
			}
		}
		return null;
	}

}
