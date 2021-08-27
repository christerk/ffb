package com.fumbbl.ffb.inducement;

/**
 * @author Kalimar
 */
public enum InducementDuration {

	UNTIL_END_OF_GAME(1, "untilEndOfGame", "For the entire game"),
	UNTIL_END_OF_DRIVE(2, "untilEndOfDrive", "For this drive"), UNTIL_END_OF_TURN(3, "untilEndOfTurn", "For this turn"),
	WHILE_HOLDING_THE_BALL(4, "whileHoldingTheBall", "While holding the ball"), UNTIL_USED(5, "untilUsed", "Single use"),
	UNTIL_END_OF_OPPONENTS_TURN(6, "untilEndOfOpponentsTurn", "For opponent's turn"),
	UNTIL_END_OF_HALF(7, "untilEndOfHalf", "For this half");

	private final int fId;
	private final String fName;
	private final String fDescription;

	InducementDuration(int pValue, String pName, String pDescription) {
		fId = pValue;
		fName = pName;
		fDescription = pDescription;
	}

	public int getId() {
		return fId;
	}

	public String getName() {
		return fName;
	}

	public String getDescription() {
		return fDescription;
	}
}
