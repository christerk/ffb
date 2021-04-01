package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.INamedObject;

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
	REGENERATION_ROLL("regenerationRoll"), SAFE_THROW_ROLL("safeThrowRoll"), TENTACLES_SHADOWING_ROLL_2020("tentaclesShadowingRoll2020"),
	TENTACLES_SHADOWING_ROLL("tentaclesShadowingRoll"), SKILL_USE("skillUse"), RE_ROLL("reRoll"), TURN_END("turnEnd"),
	PLAYER_ACTION("playerAction"), FOUL("foul"), HAND_OVER("handOver"), INJURY("injury"),
	APOTHECARY_ROLL("apothecaryRoll"), APOTHECARY_CHOICE("apothecaryChoice"), THROW_IN("throwIn"),
	SCATTER_BALL("scatterBall"), BLOCK("block"), BLOCK_CHOICE("blockChoice"), SPECTATORS("spectators"),
	WEATHER("weather"), COIN_THROW("coinThrow"), RECEIVE_CHOICE("receiveChoice"), KICKOFF_RESULT("kickoffResult"),
	KICKOFF_SCATTER("kickoffScatter"), KICKOFF_EXTRA_REROLL("extraReRoll"), KICKOFF_RIOT("kickoffRiot"),
	KICKOFF_THROW_A_ROCK("kickoffThrowARock"), PUSHBACK("pushback"), REFEREE("referee"),
	KICKOFF_PITCH_INVASION("kickoffPitchInvasion"), THROW_TEAM_MATE_ROLL("throwTeamMateRoll"),
	SCATTER_PLAYER("scatterPlayer"), SWOOP_PLAYER("swoopPlayer"), TIMEOUT_ENFORCED("timeoutEnforced"),
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
	FAN_FACTOR("fanFactor"), SELECT_BLITZ_TARGET("selectBlitzTarget"), BOMB_EXPLODES_AFTER_CATCH("bombExplodesAfterCatch");

	// obsolete: 50 (spiralling expenses)
	// obsolete: 71 (game options)

	private final String fName;

	ReportId(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

	public IReport createReport() {
		switch (this) {
			case ARGUE_THE_CALL:
				return new ReportArgueTheCallRoll();
			case ALWAYS_HUNGRY_ROLL:
				return new ReportSkillRoll(ALWAYS_HUNGRY_ROLL);
			case CARD_EFFECT_ROLL:
				return new ReportCardEffectRoll();
			case CATCH_ROLL:
				return new ReportCatchRoll();
			case CLOUD_BURSTER:
				return new ReportCloudBurster();
			case CONFUSION_ROLL:
				return new ReportConfusionRoll();
			case DAUNTLESS_ROLL:
				return new ReportDauntlessRoll();
			case DEFECTING_PLAYERS:
				return new ReportDefectingPlayers();
			case DODGE_ROLL:
				return new ReportSkillRoll(DODGE_ROLL);
			case ESCAPE_ROLL:
				return new ReportSkillRoll(ESCAPE_ROLL);
			case FAN_FACTOR_ROLL_POST_MATCH:
				return new ReportFanFactorRollPostMatch();
			case FOUL:
				return new ReportFoul();
			case FOUL_APPEARANCE_ROLL:
				return new ReportSkillRoll(FOUL_APPEARANCE_ROLL);
			case FUMBBL_RESULT_UPLOAD:
				return new ReportFumbblResultUpload();
			case GO_FOR_IT_ROLL:
				return new ReportSkillRoll(GO_FOR_IT_ROLL);
			case HAND_OVER:
				return new ReportHandOver();
			case INJURY:
				return new ReportInjury();
			case INTERCEPTION_ROLL:
				return new ReportInterceptionRoll();
			case JUMP_ROLL:
				return new ReportSkillRoll(JUMP_ROLL);
			case MOST_VALUABLE_PLAYERS:
				return new ReportMostValuablePlayers();
			case PASS_ROLL:
				return new ReportPassRoll();
			case PICK_UP_ROLL:
				return new ReportSkillRoll(PICK_UP_ROLL);
			case PLAYER_ACTION:
				return new ReportPlayerAction();
			case RE_ROLL:
				return new ReportReRoll();
			case REGENERATION_ROLL:
				return new ReportSkillRoll(REGENERATION_ROLL);
			case RIGHT_STUFF_ROLL:
				return new ReportSkillRoll(RIGHT_STUFF_ROLL);
			case SAFE_THROW_ROLL:
				return new ReportSkillRoll(SAFE_THROW_ROLL);
			case SKILL_USE:
				return new ReportSkillUse();
			case TENTACLES_SHADOWING_ROLL:
				return new ReportTentaclesShadowingRoll();
			case TENTACLES_SHADOWING_ROLL_2020:
				return new ReportTentaclesShadowingRoll2020();
			case TURN_END:
				return new ReportTurnEnd();

			// TODO: sort alphabetically

			case APOTHECARY_ROLL:
				return new ReportApothecaryRoll();
			case APOTHECARY_CHOICE:
				return new ReportApothecaryChoice();
			case THROW_IN:
				return new ReportThrowIn();
			case SCATTER_BALL:
				return new ReportScatterBall();
			case BLOCK:
				return new ReportBlock();
			case BLOCK_CHOICE:
				return new ReportBlockChoice();
			case SPECTATORS:
				return new ReportSpectators();
			case WEATHER:
				return new ReportWeather();
			case COIN_THROW:
				return new ReportCoinThrow();
			case RECEIVE_CHOICE:
				return new ReportReceiveChoice();
			case KICKOFF_RESULT:
				return new ReportKickoffResult();
			case KICKOFF_SCATTER:
				return new ReportKickoffScatter();
			case KICKOFF_EXTRA_REROLL:
				return new ReportKickoffExtraReRoll();
			case KICKOFF_RIOT:
				return new ReportKickoffRiot();
			case KICKOFF_THROW_A_ROCK:
				return new ReportKickoffThrowARock();
			case PUSHBACK:
				return new ReportPushback();
			case REFEREE:
				return new ReportReferee();
			case KICKOFF_PITCH_INVASION:
				return new ReportKickoffPitchInvasion();
			case THROW_TEAM_MATE_ROLL:
				return new ReportThrowTeamMateRoll();
			case KICK_TEAM_MATE_ROLL:
				return new ReportKickTeamMateRoll();
			case SCATTER_PLAYER:
				return new ReportScatterPlayer();
			case SWOOP_PLAYER:
				return new ReportSwoopPlayer();
			case TIMEOUT_ENFORCED:
				return new ReportTimeoutEnforced();
			case WINNINGS_ROLL:
				return new ReportWinningsRoll();
			case JUMP_UP_ROLL:
				return new ReportSkillRoll(JUMP_UP_ROLL);
			case STAND_UP_ROLL:
				return new ReportStandUpRoll();
			case BRIBES_ROLL:
				return new ReportBribesRoll();
			case MASTER_CHEF_ROLL:
				return new ReportMasterChefRoll();
			case START_HALF:
				return new ReportStartHalf();
			case INDUCEMENT:
				return new ReportInducement();
			case PILING_ON:
				return new ReportPilingOn();
			case CHAINSAW_ROLL:
				return new ReportSkillRoll(CHAINSAW_ROLL);
			case LEADER:
				return new ReportLeader();
			case SECRET_WEAPON_BAN:
				return new ReportSecretWeaponBan();
			case BLOOD_LUST_ROLL:
				return new ReportSkillRoll(BLOOD_LUST_ROLL);
			case HYPNOTIC_GAZE_ROLL:
				return new ReportSkillRoll(HYPNOTIC_GAZE_ROLL);
			case BITE_SPECTATOR:
				return new ReportBiteSpectator();
			case ANIMOSITY_ROLL:
				return new ReportSkillRoll(ANIMOSITY_ROLL);
			case RAISE_DEAD:
				return new ReportRaiseDead();
			case BLOCK_ROLL:
				return new ReportBlockRoll();
			case PENALTY_SHOOTOUT:
				return new ReportPenaltyShootout();
			case DOUBLE_HIRED_STAR_PLAYER:
				return new ReportDoubleHiredStarPlayer();
			case SPELL_EFFECT_ROLL:
				return new ReportSpecialEffectRoll();
			case WIZARD_USE:
				return new ReportWizardUse();
			case PASS_BLOCK:
				return new ReportPassBlock();
			case NO_PLAYERS_TO_FIELD:
				return new ReportNoPlayersToField();
			case PLAY_CARD:
				return new ReportPlayCard();
			case CARD_DEACTIVATED:
				return new ReportCardDeactivated();
			case BOMB_OUT_OF_BOUNDS:
				return new ReportBombOutOfBounds();
			case PETTY_CASH:
				return new ReportPettyCash();
			case FREE_PETTY_CASH:
				return new ReportFreePettyCash();
			case INDUCEMENTS_BOUGHT:
				return new ReportInducementsBought();
			case CARDS_BOUGHT:
				return new ReportCardsBought();
			case GAME_OPTIONS:
				return new ReportGameOptions();
			case WEEPING_DAGGER_ROLL:
				return new ReportSkillRoll(ReportId.WEEPING_DAGGER_ROLL);
			case RIOTOUS_ROOKIES:
				return new ReportRiotousRookies();
			case SWARMING_PLAYERS_ROLL:
				return new ReportSwarmingRoll();
			case PASS_DEVIATE:
				return new ReportPassDeviate();
			case CARDS_AND_INDUCEMENTS_BOUGHT:
				return new ReportCardsAndInducementsBought();
			case FAN_FACTOR:
				return new ReportFanFactor();
			case SELECT_BLITZ_TARGET:
				return new ReportSelectBlitzTarget();
			case BOMB_EXPLODES_AFTER_CATCH:
				return new ReportBombExplodesAfterCatch();
			default:
				throw new IllegalStateException("Unhandled report id " + getName() + ".");
		}
	}

}
