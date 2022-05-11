package com.fumbbl.ffb;

/**
 *
 * @author Kalimar
 */
public enum TurnMode implements INamedObject {

	REGULAR("regular", true), SETUP("setup"), KICKOFF("kickoff"),
	PERFECT_DEFENCE("perfectDefence"), SOLID_DEFENCE("solidDefence"),
	QUICK_SNAP("quickSnap"), HIGH_KICK("highKick"), START_GAME("startGame"), BLITZ("blitz", true),
	TOUCHBACK("touchback"), INTERCEPTION("interception"), END_GAME("endGame"),
	SWARMING("swarming"), KICKOFF_RETURN("kickoffReturn"), WIZARD("wizard"),
	PASS_BLOCK("passBlock"), DUMP_OFF("dumpOff"), NO_PLAYERS_TO_FIELD("noPlayersToField"),
	BOMB_HOME("bombHome"), BOMB_AWAY("bombAway"),
	BOMB_HOME_BLITZ("bombHomeBlitz"),
	BOMB_AWAY_BLITZ("bombAwayBlitz"),
	ILLEGAL_SUBSTITUTION("illegalSubstitution"), SELECT_BLITZ_TARGET("selectBlitzTarget"),
	SELECT_GAZE_TARGET("selectGazeTarget"), SAFE_PAIR_OF_HANDS("safePairOfHands"),
	BETWEEN_TURNS("betweenTurns", true), RAIDING_PARTY("raidingParty");

	private final String fName;
	private final boolean checkForActivePlayers;

	TurnMode(String pName) {
		this(pName, false);
	}

	TurnMode(String pName, boolean checkForActivePlayers) {
		fName = pName;
		this.checkForActivePlayers = checkForActivePlayers;
	}

	public String getName() {
		return fName;
	}

	public boolean checkNegatraits() {
		return ((this != KICKOFF_RETURN) && (this != PASS_BLOCK) && !isBombTurn());
	}

	public boolean isBombTurn() {
		return ((this == BOMB_HOME) || (this == BOMB_HOME_BLITZ) || (this == BOMB_AWAY) || (this == BOMB_AWAY_BLITZ));
	}

	public boolean isCheckForActivePlayers() {
		return checkForActivePlayers;
	}
}
