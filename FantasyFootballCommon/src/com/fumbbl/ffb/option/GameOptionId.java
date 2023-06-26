package com.fumbbl.ffb.option;

import com.fumbbl.ffb.INamedObject;

/**
 * @author Kalimar
 */
public enum GameOptionId implements INamedObject {
	RULESVERSION("rulesVersion"),
	CHECK_OWNERSHIP("checkOwnership"), TEST_MODE("testMode"),
	OVERTIME("overtime"), TURNTIME("turntime"),
	ALLOW_CONCESSIONS("allowConcessions"),

	PETTY_CASH("pettyCash"),
	INDUCEMENTS("inducements"),
	MAX_NR_OF_CARDS("maxNrOfCards"),

	MAX_PLAYERS_ON_FIELD("maxPlayersOnField"), MAX_PLAYERS_IN_WIDE_ZONE("maxPlayersInWideZone"),
	MIN_PLAYERS_ON_LOS("minPlayersOnLos"),

	ALLOW_STAR_ON_BOTH_TEAMS("allowStarOnBothTeams"),
	ALLOW_STAFF_ON_BOTH_TEAMS("allowStaffOnBothTeams"),
	FORCE_TREASURY_TO_PETTY_CASH("forceTreasuryToPettyCash"),
	USE_PREDEFINED_INDUCEMENTS("usePredefinedInducements"),

	ALLOW_KTM_REROLL("allowKtmReroll"), CLAW_DOES_NOT_STACK("clawDoesNotStack"), FOUL_BONUS("foulBonus"),
	FOUL_BONUS_OUTSIDE_TACKLEZONE("foulBonusOutsideTacklezone"), FREE_INDUCEMENT_CASH("freeInducementCash"),
	FREE_CARD_CASH("freeCardCash"), PILING_ON_DOES_NOT_STACK("pilingOnDoesNotStack"),
	PILING_ON_INJURY_ONLY("pilingOnInjuryOnly"), PILING_ON_ARMOR_ONLY("pilingOnArmorOnly"),
	PILING_ON_TO_KO_ON_DOUBLE("pilingOnToKoOnDouble"), PILING_ON_USES_A_TEAM_REROLL("pilingOnUsesATeamReroll"),
	RIGHT_STUFF_CANCELS_TACKLE("rightStuffCancelsTackle"), SNEAKY_GIT_AS_FOUL_GUARD("sneakyGitAsFoulGuard"),
	SNEAKY_GIT_BAN_TO_KO("sneakyGitBanToKo"), STAND_FIRM_NO_DROP_ON_FAILED_DODGE("standFirmNoDropOnFailedDodge"),
	SPIKED_BALL("spikedBall"),

	ARGUE_THE_CALL("argueTheCall"), MVP_NOMINATIONS("mvpNominations"), PETTY_CASH_AFFECTS_TV("pettyCashAffectsTv"),
	WIZARD_AVAILABLE("wizardAvailable"),

	EXTRA_MVP("extraMvp"),

	CARDS_MISCELLANEOUS_MAYHEM_COST("cardsMiscellaneousMayhemCost"),
	CARDS_MISCELLANEOUS_MAYHEM_MAX("cardsMiscellaneousMayhemMax"),
	CARDS_SPECIAL_TEAM_PLAY_COST("cardsSpecialTeamPlayCost"), CARDS_SPECIAL_TEAM_PLAY_MAX("cardsSpecialTeamPlayMax"),
	CARDS_MAGIC_ITEM_COST("cardsMagicItemCost"), CARDS_MAGIC_ITEM_MAX("cardsMagicItemMax"),
	CARDS_DIRTY_TRICK_COST("cardsDirtyTrickCost"), CARDS_DIRTY_TRICK_MAX("cardsDirtyTrickMax"),
	CARDS_GOOD_KARMA_COST("cardsGoodKarmaCost"), CARDS_GOOD_KARMA_MAX("cardsGoodKarmaMax"),
	CARDS_RANDOM_EVENT_COST("cardsRandomEventCost"), CARDS_RANDOM_EVENT_MAX("cardsRandomEventMax"),
	CARDS_DESPERATE_MEASURE_COST("cardsDesperateMeasureCost"), CARDS_DESPERATE_MEASURE_MAX("cardsDesperateMeasureMax"),
	CARDS_SPECIAL_PLAY_COST("cardsSpecialPlayCost"),

	INDUCEMENT_APOS_COST("inducementAposCost"), INDUCEMENT_APOS_MAX("inducementAposMax"),
	INDUCEMENT_BRIBES_COST("inducementBribesCost"), INDUCEMENT_BRIBES_REDUCED_COST("inducementBribesReducedCost"),
	INDUCEMENT_BRIBES_MAX("inducementBribesMax"), INDUCEMENT_CHEFS_COST("inducementChefsCost"),
	INDUCEMENT_CHEFS_REDUCED_COST("inducementChefsReducedCost"), INDUCEMENT_CHEFS_MAX("inducementChefsMax"),
	INDUCEMENT_EXTRA_TRAINING_COST("inducementExtraTrainingCost"),
	INDUCEMENT_EXTRA_TRAINING_MAX("inducementExtraTrainingMax"), INDUCEMENT_IGORS_COST("inducementIgorsCost"),
	INDUCEMENT_IGORS_MAX("inducementIgorsMax"), INDUCEMENT_MORTUARY_ASSISTANTS_COST("inducementMortuaryAssistantsCost"),
	INDUCEMENT_MORTUARY_ASSISTANTS_MAX("inducementMortuaryAssistantsMax"), INDUCEMENT_PLAGUE_DOCTORS_COST("inducementPlagueDoctorsCost"),
	INDUCEMENT_PLAGUE_DOCTORS_MAX("inducementPlagueDoctorsMax"), INDUCEMENT_KEGS_COST("inducementKegsCost"),
	INDUCEMENT_KEGS_MAX("inducementKegsMax"), INDUCEMENT_MERCENARIES_EXTRA_COST("inducementMercenariesExtraCost"),
	INDUCEMENT_MERCENARIES_SKILL_COST("inducementMercenariesSkillCost"),
	INDUCEMENT_MERCENARIES_MAX("inducementMercenariesMax"),
	INDUCEMENT_PRAYERS_COST("inducementPrayersCost"), INDUCEMENT_PRAYERS_MAX("inducementPrayersMax"),
	INDUCEMENT_PRAYERS_USE_LEAGUE_TABLE("inducementPrayersUseLeagueTable"), INDUCEMENT_PRAYERS_AVAILABLE_FOR_UNDERDOG("inducementPrayersAvailableForUnderdog"),
	INDUCEMENT_RIOTOUS_ROOKIES_COST("inducementRiotousRookiesCost"),
	INDUCEMENT_RIOTOUS_ROOKIES_MAX("inducementRiotousRookiesMax"), INDUCEMENT_STARS_MAX("inducementStarsMax"),
	INDUCEMENT_STAFF_MAX("inducementStaffMax"),
	INDUCEMENT_WIZARDS_COST("inducementWizardsCost"), INDUCEMENT_WIZARDS_MAX("inducementWizardsMax"),
	INDUCEMENT_BIASED_REF_COST("inducementBiasedRefCost"), INDUCEMENT_BIASED_REF_REDUCED_COST("inducementBiasedRefReducedCost"), INDUCEMENT_BIASED_REF_MAX("inducementBiasedRefMax"),
	INDUCEMENT_TEMP_CHEERLEADER_MAX("inducementTempCheerleaderMax"), INDUCEMENT_TEMP_CHEERLEADER_TOTAL_MAX("inducementTempCheerleaderTotalMax"), INDUCEMENT_TEMP_CHEERLEADER_COST("inducementTempCheerleaderCost"),
	INDUCEMENT_PART_TIME_COACH_MAX("inducementPartTimeCoachMax"), INDUCEMENT_PART_TIME_COACH_TOTAL_MAX("inducementPartTimeCoachTotalMax"), INDUCEMENT_PART_TIME_COACH_COST("inducementPartTimeCoachCost"),
	INDUCEMENT_WEATHER_MAGE_MAX("inducementWeatherMageMax"), INDUCEMENT_WEATHER_MAGE_COST("inducementWeatherMageCost"),
	INDUCEMENTS_ALLOW_SPENDING_TREASURY_ON_EQUAL_CTV("inducementsAllowSpendingTreasuryOnEqualCTV"),
	INDUCEMENTS_ALWAYS_USE_TREASURY("inducementsAlwaysUseTreasury"), INDUCEMENTS_ALLOW_OVERDOG_SPENDING("inducementsAllowOverdogSpending"),

	ENABLE_STALLING_CHECK("enableStallingCheck"), ALLOW_BALL_AND_CHAIN_RE_ROLL("allowBallAndChainReRoll"),
	END_TURN_WHEN_HITTING_ANY_PLAYER_WITH_TTM("endTurnWhenHittingAnyPlayerWithTtm"), SWOOP_DISTANCE("swoopDistance"),
	ALLOW_SPECIAL_BLOCKS_WITH_BALL_AND_CHAIN("allowSpecialBlocksWithBallAndChain"),
	CHAINSAW_TURNOVER_ON_AV_BREAK("chainsawTurnoverOnAvBreak"), // legacy, keep around to make sure old replays or running games do not break after update
	CHAINSAW_TURNOVER("chainsawTurnover"), BOMBER_PLACED_PRONE_IGNORES_TURNOVER("bomberPlacedProneIgnoresTurnover"),
	SNEAKY_GIT_CAN_MOVE_AFTER_FOUL("sneakyGitCanMoveAfterFoul"), BOMB_USES_MB("bombUsesMb"),
	OVERTIME_GOLDEN_GOAL("overtimeGoldenGoal"), OVERTIME_KICK_OFF_RESULTS("overtimeKickOffResults"),

	PITCH_URL("pitchUrl");

	private final String fName;

	GameOptionId(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
