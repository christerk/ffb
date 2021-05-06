package com.fumbbl.ffb;

/**
 *
 * @author Kalimar
 */
public enum TurnMode implements INamedObject {

	REGULAR("regular", true), SETUP("setup", true), KICKOFF("kickoff", true),
	PERFECT_DEFENCE("perfectDefence", true), SOLID_DEFENCE("solidDefence", true),
	QUICK_SNAP("quickSnap", true), HIGH_KICK("highKick", true), START_GAME("startGame", true), BLITZ("blitz", true),
	TOUCHBACK("touchback", true), INTERCEPTION("interception", true), END_GAME("endGame", true),
	SWARMING("swarming", true), KICKOFF_RETURN("kickoffReturn", true), WIZARD("wizard", true),
	PASS_BLOCK("passBlock", true), DUMP_OFF("dumpOff", true), NO_PLAYERS_TO_FIELD("noPlayersToField", true),
	BOMB_HOME("bombHome", false), BOMB_AWAY("bombAway", false),
	BOMB_HOME_BLITZ("bombHomeBlitz", false), // TODO: use game.getLastTurnMode() for this
	BOMB_AWAY_BLITZ("bombAwayBlitz", false), // TODO: use game.getLastTurnMode() for this
	ILLEGAL_SUBSTITUTION("illegalSubstitution", true),
	SELECT_BLITZ_TARGET("selectBlitzTarget", true), SAFE_PAIR_OF_HANDS("safePairOfHands", true);

	private final String fName;
	private boolean fStoreLast;

	TurnMode(String pName, boolean storeLast) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

	public boolean isStoreLast() {
		return fStoreLast;
	}

	public boolean checkNegatraits() {
		return ((this != KICKOFF_RETURN) && (this != PASS_BLOCK) && !isBombTurn());
	}

	public boolean isBombTurn() {
		return ((this == BOMB_HOME) || (this == BOMB_HOME_BLITZ) || (this == BOMB_AWAY) || (this == BOMB_AWAY_BLITZ));
	}

}
