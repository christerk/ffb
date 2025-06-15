package com.fumbbl.ffb.dialog;

import com.fumbbl.ffb.INamedObject;

/**
 * @author Kalimar
 */
public enum DialogId implements INamedObject {

	INFORMATION("information"), YES_OR_NO_QUESTION("yesOrNoQuestion"), GAME_COACH_PASSWORD("gameCoachPassword"),
	TEAM_CHOICE("teamChoice"), COIN_CHOICE("coinChoice"), RE_ROLL("reRoll"),
	RE_ROLL_FOR_TARGETS("reRollForTargets"), RE_ROLL_BLOCK_FOR_TARGETS("reRollBlockForTargets"), SKILL_USE("skillUse"),
	PROGRESS_BAR("progressBar"), TEAM_SETUP("teamSetup"), USE_APOTHECARY("useApothecary"),
	USE_APOTHECARIES("useApothecaries"), USE_IGORS("useIgors"), USE_MORTUARY_ASSISTANTS("useMortuaryAssistants"),
	RECEIVE_CHOICE("receiveChoice"), FOLLOWUP_CHOICE("followupChoice"), START_GAME("startGame"),
	APOTHECARY_CHOICE("apothecaryChoice"), TOUCHBACK("touchback"), INTERCEPTION("interception"),
	SETUP_ERROR("setupError"), GAME_STATISTICS("gameStatistics"), WINNINGS_RE_ROLL("winningsReRoll"),
	GAME_CHOICE("gameChoice"), KEY_BINDINGS("keyBindings"), BLOCK_ROLL("blockRoll"), PLAYER_CHOICE("playerChoice"),
	DEFENDER_ACTION("defenderAction"), JOIN("join"), CONCEDE_GAME("concedeGame"), ABOUT("about"), END_TURN("endTurn"),
	LEAVE_GAME("leaveGame"), BRIBES("bribes"), PILING_ON("pilingOn"), BUY_INDUCEMENTS("buyInducements"),
	SOUND_VOLUME("soundVolume"), JOURNEYMEN("journeymen"), SCALING_FACTOR("scalingFactor"),
	CHAT_COMMANDS("chatCommands"), KICK_SKILL("kickSkill"), USE_IGOR("useIgor"), USE_MORTUARY_ASSISTANT("useMortuaryAssistant"),
	KICKOFF_RETURN("kickoffReturn"), SWARMING("swarming"), SWARMING_ERROR("swarmingError"), PETTY_CASH("pettyCash"),
	WIZARD_SPELL("wizardSpell"), USE_INDUCEMENT("useInducement"), PASS_BLOCK("passBlock"), BUY_CARDS("buyCards"),
	BUY_CARDS_AND_INDUCEMENTS("buyCardsAndInducements"), ARGUE_THE_CALL("argueTheCall"), SELECT_BLITZ_TARGET("selectBlitzTarget"),
	OPPONENT_BLOCK_SELECTION("opponentBlockSelection"), PILE_DRIVER("pileDriver"), USE_CHAINSAW("useChainsaw"),
	BLOCK_ROLL_PARTIAL_RE_ROLL("blockRollPartialReRoll"), INVALID_SOLID_DEFENCE("invalidSolidDefence"), SELECT_SKILL("selectSkill"),
	BRIBERY_AND_CORRUPTION_RE_ROLL("briberyAndCorruptionReRoll"), SELECT_GAZE_TARGET("selectGazeTarget"),
	CONFIRM_END_ACTION("confirmEndAction"), CHANGE_LIST("changeList"), SELECT_WEATHER("selectWeather"),
	INFORMATION_OKAY("informationOkay"), STORE_PROPERTIES_LOCAL("storePropertiesLocal"), KICK_OFF_RESULT("kickOffResult"),
	BLOODLUST_ACTION("bloodlustAction"), PENALTY_SHOOTOUT("penaltyShootout"), REPLAY_MODE_CHOICE("replayModeChoice");

	private final String fName;

	DialogId(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
