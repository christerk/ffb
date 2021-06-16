package com.fumbbl.ffb.report;

import com.fumbbl.ffb.INamedObject;

/**
 * @author Kalimar
 */
public enum ReportId implements INamedObject {

	NONE("none"),
	
	// Internal reports
	NERVES_OF_STEEL("nervesOfSteel"),
	
	// On-wire reports
	ALWAYS_HUNGRY_ROLL("alwaysHungryRoll"), ARGUE_THE_CALL("argueTheCall"), CATCH_ROLL("catchRoll"), CLOUD_BURSTER("cloudBurster"),
	CONFUSION_ROLL("confusionRoll"), DAUNTLESS_ROLL("dauntlessRoll"), DODGE_ROLL("dodgeRoll"), ESCAPE_ROLL("escapeRoll"),
	FOUL_APPEARANCE_ROLL("foulAppearanceRoll"), GO_FOR_IT_ROLL("goForItRoll"), INTERCEPTION_ROLL("interceptionRoll"),
	JUMP_ROLL("leapRoll"), PASS_ROLL("passRoll"), PICK_UP_ROLL("pickUpRoll"), RIGHT_STUFF_ROLL("rightStuffRoll"),
	REGENERATION_ROLL("regenerationRoll"), SAFE_THROW_ROLL("safeThrowRoll"),
	TENTACLES_SHADOWING_ROLL("tentaclesShadowingRoll"), SKILL_USE("skillUse"), RE_ROLL("reRoll"), TURN_END("turnEnd"),
	PLAYER_ACTION("playerAction"), FOUL("foul"), HAND_OVER("handOver"), INJURY("injury"),
	APOTHECARY_ROLL("apothecaryRoll"), APOTHECARY_CHOICE("apothecaryChoice"), THROW_IN("throwIn"),
	SCATTER_BALL("scatterBall"), BLOCK("block"), BLOCK_CHOICE("blockChoice"), SPECTATORS("spectators"),
	WEATHER("weather"), COIN_THROW("coinThrow"), RECEIVE_CHOICE("receiveChoice"), KICKOFF_RESULT("kickoffResult"),
	KICKOFF_SCATTER("kickoffScatter"), KICKOFF_EXTRA_REROLL("extraReRoll"), KICKOFF_RIOT("kickoffRiot"),
	KICKOFF_TIMEOUT("kickoffTimeout"), SOLID_DEFENCE_ROLL("solidDefenceRoll"), QUICK_SNAP_ROLL("quickSnapRoll"),
	KICKOFF_SEQUENCE_ACTIVATIONS_COUNT("kickoffSequenceActivationsCount"), KICKOFF_SEQUENCE_ACTIVATIONS_EXHAUSTED("kickoffSequenceActivationsExhausted"),
	BLITZ_ROLL("blitzRoll"), KICKOFF_OFFICIOUIS_REF("kickoffOfficiousRef"), OFFICIOUS_REF_ROLL("officiousRefRoll"),
	KICKOFF_THROW_A_ROCK("kickoffThrowARock"), PUSHBACK("pushback"), REFEREE("referee"),
	KICKOFF_PITCH_INVASION("kickoffPitchInvasion"), THROW_TEAM_MATE_ROLL("throwTeamMateRoll"), DEDICATED_FANS("dedicatedFans"),
	SCATTER_PLAYER("scatterPlayer"), SWOOP_PLAYER("swoopPlayer"), TIMEOUT_ENFORCED("timeoutEnforced"), WINNINGS("winnings"),
	WINNINGS_ROLL("winningsRoll"), FUMBBL_RESULT_UPLOAD("fumbblResultUpload"), FAN_FACTOR_ROLL_POST_MATCH("fanFactorRoll"),
	MOST_VALUABLE_PLAYERS("mostValuablePlayers"), DEFECTING_PLAYERS("defectingPlayers"), JUMP_UP_ROLL("jumpUpRoll"),
	STAND_UP_ROLL("standUpRoll"), BRIBES_ROLL("bribesRoll"), MASTER_CHEF_ROLL("masterChefRoll"), START_HALF("startHalf"),
	INDUCEMENT("inducement"), PILING_ON("pilingOn"), CHAINSAW_ROLL("chainsawRoll"), LEADER("leader"),
	SECRET_WEAPON_BAN("secretWeaponBan"), BLOOD_LUST_ROLL("bloodLustRoll"), HYPNOTIC_GAZE_ROLL("hypnoticGazeRoll"),
	BITE_SPECTATOR("biteSpectator"), ANIMOSITY_ROLL("animosityRoll"), RAISE_DEAD("raiseDead"), BLOCK_ROLL("blockRoll"),
	PENALTY_SHOOTOUT("penaltyShootout"), DOUBLE_HIRED_STAR_PLAYER("doubleHiredStarPlayer"),
	SPELL_EFFECT_ROLL("spellEffectRoll"), WIZARD_USE("wizardUse"), GAME_OPTIONS("gameOptions"), // only for conversion
	PASS_BLOCK("passBlock"), NO_PLAYERS_TO_FIELD("noPlayersToField"), PLAY_CARD("playCard"),
	CARD_DEACTIVATED("cardDeactivated"), BOMB_OUT_OF_BOUNDS("bombOutOfBounds"), PETTY_CASH("pettyCash"), FREE_PETTY_CASH("freePettyCash"),
	INDUCEMENTS_BOUGHT("inducementsBought"), CARDS_BOUGHT("cardsBought"), CARD_EFFECT_ROLL("cardEffectRoll"),
	WEEPING_DAGGER_ROLL("weepingDaggerRoll"), KICK_TEAM_MATE_ROLL("kickTeamMateRoll"), RIOTOUS_ROOKIES("riotousRookies"),
	SWARMING_PLAYERS_ROLL("swarmingPlayersRoll"), PASS_DEVIATE("passDeviate"), CARDS_AND_INDUCEMENTS_BOUGHT("cardsAndInducementsBought"),
	FAN_FACTOR("fanFactor"), SELECT_BLITZ_TARGET("selectBlitzTarget"), BOMB_EXPLODES_AFTER_CATCH("bombExplodesAfterCatch"),
	USE_BRAWLER("useBrawler"), PLACE_BALL_DIRECTION("placedBallDirection"), FUMBLEROOSKIE("fumblerooskie"),
	ANIMAL_SAVAGERY("animalSavagery"), PROJECTILE_VOMIT("projectileVomit"), BRILLIANT_COACHING_REROLLS_LOST("brilliantCoachingReRoll"),
	KICK_TEAM_MATE_FUMBLE("kickTeamMateFumble"), BLOCK_RE_ROLL("blockReRoll"), TRAP_DOOR("trapDoor"),
	PRAYER_AMOUNT("prayerAmount"), PRAYER_ROLL("prayerRoll"), PRAYER_END("prayerEnd"), KICKOFF_CHEERING_FANS("cheeringFans");

	// obsolete: 50 (spiralling expenses)
	// obsolete: 71 (game options)

	private final String fName;

	ReportId(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}
}
