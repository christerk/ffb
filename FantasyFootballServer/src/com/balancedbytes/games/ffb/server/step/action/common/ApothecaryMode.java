package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.IEnumWithId;
import com.balancedbytes.games.ffb.IEnumWithName;

/**
 * 
 * @author Kalimar
 */
public enum ApothecaryMode implements IEnumWithId, IEnumWithName {
	
	ATTACKER(1, "attacker"),
	AWAY(2, "away"),
	CROWD_PUSH(3, "crowdPush"),
	DEFENDER(4, "defender"),
	FEEDING(5, "feeding"),
	HOME(6, "home"),
	SPECIAL_EFFECT(7, "specialEffect"),
	THROWN_PLAYER(8, "thrownPlayer"),
	HIT_PLAYER(9, "hitPlayer"),
	CATCHER(10, "catcher");
	
	private int fId;
	private String fName;
	
	private ApothecaryMode(int pId, String pName) {
		fId = pId;
		fName = pName;
	}
	
	public int getId() {
		return fId;
	}
	
	public String getName() {
	  return fName;
	}

}
