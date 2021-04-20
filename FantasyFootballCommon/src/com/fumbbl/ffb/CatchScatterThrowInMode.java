package com.fumbbl.ffb;

/**
 * 
 * @author Kalimar
 */
public enum CatchScatterThrowInMode implements INamedObject {

	CATCH_ACCURATE_BOMB("catchAccurateBomb", true),
	CATCH_ACCURATE_BOMB_EMPTY_SQUARE("catchAccurateBombEmptySquare", true),
	CATCH_ACCURATE_PASS("catchAccuratePass", false),
	CATCH_ACCURATE_PASS_EMPTY_SQUARE("catchAccuratePassEmptySquare", false),
	CATCH_BOMB("catchBomb", true),
	CATCH_HAND_OFF("catchHandOff", false),
	CATCH_KICKOFF("catchKickoff", false),
	CATCH_MISSED_PASS("catchMissedPass", false),
	CATCH_SCATTER("catchScatter", false),
	CATCH_THROW_IN("catchThrowIn", false),
	DEFLECTED("deflected", false),
	DEFLECTED_BOMB("deflectedBomb", true),
	FAILED_CATCH("failedCatch", false),
	FAILED_PICK_UP("failedPickUp", false),
	SCATTER_BALL("scatterBall", false),
	THROW_IN("throwIn", false);

	private final String fName;
	private final boolean fBomb;

	CatchScatterThrowInMode(String pName, boolean pBomb) {
		fName = pName;
		fBomb = pBomb;
	}

	public String getName() {
		return fName;
	}

	public boolean isBomb() {
		return fBomb;
	}

}
