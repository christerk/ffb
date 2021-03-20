package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.INamedObject;

/**
 *
 * @author Kalimar
 */
public enum DialogId implements INamedObject {

	INFORMATION("information"), YES_OR_NO_QUESTION("yesOrNoQuestion"), GAME_COACH_PASSWORD("gameCoachPassword"),
	TEAM_CHOICE("teamChoice"), COIN_CHOICE("coinChoice"), RE_ROLL("reRoll"), SKILL_USE("skillUse"),
	PROGRESS_BAR("progressBar"), TEAM_SETUP("teamSetup"), USE_APOTHECARY("useApothecary"),
	RECEIVE_CHOICE("receiveChoice"), FOLLOWUP_CHOICE("followupChoice"), START_GAME("startGame"),
	APOTHECARY_CHOICE("apothecaryChoice"), TOUCHBACK("touchback"), INTERCEPTION("interception"),
	SETUP_ERROR("setupError"), GAME_STATISTICS("gameStatistics"), WINNINGS_RE_ROLL("winningsReRoll"),
	GAME_CHOICE("gameChoice"), KEY_BINDINGS("keyBindings"), BLOCK_ROLL("blockRoll"), PLAYER_CHOICE("playerChoice"),
	DEFENDER_ACTION("defenderAction"), JOIN("join"), CONCEDE_GAME("concedeGame"), ABOUT("about"), END_TURN("endTurn"),
	LEAVE_GAME("leaveGame"), BRIBES("bribes"), PILING_ON("pilingOn"), BUY_INDUCEMENTS("buyInducements"),
	SOUND_VOLUME("soundVolume"), JOURNEYMEN("journeymen"),
	KICKOFF_RESULT("kickoffResult"), CHAT_COMMANDS("chatCommands"), KICK_SKILL("kickSkill"), USE_IGOR("useIgor"),
	KICKOFF_RETURN("kickoffReturn"), SWARMING("swarming"), SWARMING_ERROR("swarmingError"), PETTY_CASH("pettyCash"),
	WIZARD_SPELL("wizardSpell"), USE_INDUCEMENT("useInducement"), PASS_BLOCK("passBlock"), BUY_CARDS("buyCards"),
	BUY_CARDS_AND_INDUCEMENTS("buyCardsAndInducements"), ARGUE_THE_CALL("argueTheCall"), SELECT_BLITZ_TARGET("selectBlitzTarget");

	private String fName;

	private DialogId(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
