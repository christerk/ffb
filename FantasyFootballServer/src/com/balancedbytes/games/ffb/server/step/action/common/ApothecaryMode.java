package com.balancedbytes.games.ffb.server.step.action.common;

import com.balancedbytes.games.ffb.IEnumWithName;

/**
 * 
 * @author Kalimar
 */
public enum ApothecaryMode implements IEnumWithName {
	
	ATTACKER("attacker"),
	AWAY("away"),
	CROWD_PUSH("crowdPush"),
	DEFENDER("defender"),
	FEEDING("feeding"),
	HOME("home"),
	SPECIAL_EFFECT("specialEffect"),
	THROWN_PLAYER("thrownPlayer"),
	HIT_PLAYER("hitPlayer"),
	CATCHER("catcher");
	
	private String fName;
	
	private ApothecaryMode(String pName) {
		fName = pName;
	}
	
	public String getName() {
	  return fName;
	}

}
