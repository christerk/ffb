package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.INamedObject;

/**
 * 
 * @author Kalimar
 */
public enum GameOptionId implements INamedObject {
  
  OVERTIME("overtime"), 
  TURNTIME("turntime"),
  PETTY_CASH("pettyCash"),
  INDUCEMENTS("inducements"),
  CHECK_OWNERSHIP("checkOwnership"),
  TEST_MODE("testMode"),
  MAX_NR_OF_CARDS("maxNrOfCards"),
  
  MAX_PLAYERS_ON_FIELD("maxPlayersOnField"),
  MAX_PLAYERS_IN_WIDE_ZONE("maxPlayersInWideZone"),
  MIN_PLAYERS_ON_LOS("minPlayersOnLos"),
  
  ALLOW_STAR_ON_BOTH_TEAMS("allowStarOnBothTeams"),
  FORCE_TREASURY_TO_PETTY_CASH("forceTreasuryToPettyCash"),
  USE_PREDEFINED_INDUCEMENTS("usePredefinedInducements"),

  ALLOW_KTM_REROLL("allowKtmReroll"),
  CLAW_DOES_NOT_STACK("clawDoesNotStack"),
  FOUL_BONUS("foulBonus"),
  FOUL_BONUS_OUTSIDE_TACKLEZONE("foulBonusOutsideTacklezone"),
  FREE_INDUCEMENT_CASH("freeInducementCash"),
  FREE_CARD_CASH("freeCardCash"),
  PILING_ON_DOES_NOT_STACK("pilingOnDoesNotStack"),
  PILING_ON_INJURY_ONLY("pilingOnInjuryOnly"),
  PILING_ON_ARMOR_ONLY("pilingOnArmorOnly"),
  PILING_ON_TO_KO_ON_DOUBLE("pilingOnToKoOnDouble"),
  PILING_ON_USES_A_TEAM_REROLL("pilingOnUsesATeamReroll"),
  RIGHT_STUFF_CANCELS_TACKLE("rightStuffCancelsTackle"),
  SNEAKY_GIT_AS_FOUL_GUARD("sneakyGitAsFoulGuard"),
  SNEAKY_GIT_BAN_TO_KO("sneakyGitBanToKo"),
  STAND_FIRM_NO_DROP_ON_FAILED_DODGE("standFirmNoDropOnFailedDodge"),
  SPIKED_BALL("spikedBall"),
  
  ARGUE_THE_CALL("argueTheCall"),
  MVP_NOMINATIONS("mvpNominations"),
  PETTY_CASH_AFFECTS_TV("pettyCashAffectsTv"),
  WIZARD_AVAILABLE("wizardAvailable"),

  EXTRA_MVP("extraMvp"),

  CARDS_MISCELLANEOUS_MAYHEM_COST("cardsMiscellaneousMayhemCost"),
  CARDS_MISCELLANEOUS_MAYHEM_MAX("cardsMiscellaneousMayhemMax"),
  CARDS_SPECIAL_TEAM_PLAY_COST("cardsSpecialTeamPlayCost"),
  CARDS_SPECIAL_TEAM_PLAY_MAX("cardsSpecialTeamPlayMax"),
  CARDS_MAGIC_ITEM_COST("cardsMagicItemCost"),
  CARDS_MAGIC_ITEM_MAX("cardsMagicItemMax"),
  CARDS_DIRTY_TRICK_COST("cardsDirtyTrickCost"),
  CARDS_DIRTY_TRICK_MAX("cardsDirtyTrickMax"),
  CARDS_GOOD_KARMA_COST("cardsGoodKarmaCost"),
  CARDS_GOOD_KARMA_MAX("cardsGoodKarmaMax"),
  CARDS_RANDOM_EVENT_COST("cardsRandomEventCost"),
  CARDS_RANDOM_EVENT_MAX("cardsRandomEventMax"),
  CARDS_DESPERATE_MEASURE_COST("cardsDesperateMeasureCost"),
  CARDS_DESPERATE_MEASURE_MAX("cardsDesperateMeasureMax"),

  INDUCEMENT_APOS_COST("inducementAposCost"),
  INDUCEMENT_APOS_MAX("inducementAposMax"),
  INDUCEMENT_BRIBES_COST("inducementBribesCost"),
  INDUCEMENT_BRIBES_REDUCED_COST("inducementBribesReducedCost"),
  INDUCEMENT_BRIBES_MAX("inducementBribesMax"),
  INDUCEMENT_CHEFS_COST("inducementChefsCost"),
  INDUCEMENT_CHEFS_REDUCED_COST("inducementChefsReducedCost"),
  INDUCEMENT_CHEFS_MAX("inducementChefsMax"),
  INDUCEMENT_EXTRA_TRAINING_COST("inducementExtraTrainingCost"),
  INDUCEMENT_EXTRA_TRAINING_MAX("inducementExtraTrainingMax"),
  INDUCEMENT_IGORS_COST("inducementIgorsCost"),
  INDUCEMENT_IGORS_MAX("inducementIgorsMax"),
  INDUCEMENT_KEGS_COST("inducementKegsCost"),
  INDUCEMENT_KEGS_MAX("inducementKegsMax"),
  INDUCEMENT_MERCENARIES_EXTRA_COST("inducementMercenariesExtraCost"),
  INDUCEMENT_MERCENARIES_SKILL_COST("inducementMercenariesSkillCost"),
  INDUCEMENT_MERCENARIES_MAX("inducementMercenariesMax"),
  INDUCEMENT_STARS_MAX("inducementStarsMax"),
  INDUCEMENT_WIZARDS_COST("inducementWizardsCost"),
  INDUCEMENT_WIZARDS_MAX("inducementWizardsMax"),

  PITCH_URL("pitchUrl");
  
  private String fName;

  private GameOptionId(String pName) {
    fName = pName;
  }

  public String getName() {
    return fName;
  }
      
}
