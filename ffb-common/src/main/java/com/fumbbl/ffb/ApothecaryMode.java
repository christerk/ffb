package com.fumbbl.ffb;

/**
 * @author Kalimar
 */
public enum ApothecaryMode implements INamedObject {

	ATTACKER("attacker"), AWAY("away"), CROWD_PUSH("crowdPush"), DEFENDER("defender"), FEEDING("feeding"), HOME("home"),
	SPECIAL_EFFECT("specialEffect"), THROWN_PLAYER("thrownPlayer"), KICKED_PLAYER("kickedPlayer"),
	HIT_PLAYER("hitPlayer"), CATCHER("catcher"), TRAP_DOOR("trapDoor"), ANIMAL_SAVAGERY("animalSavagery"),
	DROPPED_BY_OWN_SKILL("droppedByOwnPlayer");

	private final String fName;

	ApothecaryMode(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
