package com.fumbbl.ffb;

/**
 *
 * @author Kalimar
 */
public enum ClientStateId implements INamedObject {

	LOGIN("login"), RE_ROLL("reRoll"), START_GAME("startGame"), SELECT_PLAYER("selectPlayer"), MOVE("move"),
	BLOCK("block"), BLITZ("blitz"), HAND_OVER("handOver"), PASS("pass"), SPECTATE("spectate"), SETUP("setup"),
	KICKOFF("kickoff"), PUSHBACK("pushback"), INTERCEPTION("interception"), FOUL("foul"), HIGH_KICK("highKick"),
	QUICK_SNAP("quickSnap"), TOUCHBACK("touchback"), WAIT_FOR_OPPONENT("waitForOpponent"), REPLAY("replay"),
	THROW_TEAM_MATE("throwTeamMate"), KICK_TEAM_MATE("kickTeamMate"), SWOOP("swoop"), DUMP_OFF("dumpOff"),
	WAIT_FOR_SETUP("waitForSetup"), GAZE("gaze"), KICKOFF_RETURN("kickoffReturn"), SWARMING("swarming"), WIZARD("wizard"),
	PASS_BLOCK("passBlock"), BOMB("bomb"), ILLEGAL_SUBSTITUTION("illegalSubstitution"), GAZE_MOVE("gazeMove"),
	SELECT_BLITZ_TARGET("selectBlitzTarget"), SELECT_GAZE_TARGET("selectGazeTarget"), SYNCHRONOUS_MULTI_BLOCK("synchronousMultiBlock"),
	PLACE_BALL("safePairOfHands"), SOLID_DEFENCE("solidDefence"), KICK_TEAM_MATE_THROW("kickTeamMateThrow"), THROW_KEG("throwKeg"),
	RAIDING_PARTY("raidingParty"), SELECT_BLOCK_KIND("selectBlockKind"), MAXIMUM_CARNAGE("maximumCarnage"),
	HIT_AND_RUN("hitAndRun");

	private final String fName;

	ClientStateId(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

	public String toString() {
		return getName();
	}

}
