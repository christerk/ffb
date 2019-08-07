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

  INDUCEMENT_BABES_MAX("inducementBabesMax"),
  INDUCEMENT_BRIBES_MAX("inducementBribesMax"),
  INDUCEMENT_CHEFS_MAX("inducementChefsMax"),
  INDUCEMENT_IGORS_MAX("inducementIgorsMax"),
  INDUCEMENT_APOS_MAX("inducementAposMax"),
  INDUCEMENT_WIZARDS_MAX("inducementWizardsMax"),
  INDUCEMENT_STARS_MAX("inducementStarsMax"),

  INDUCEMENT_BABE_COST("inducementBabeCost"),
  INDUCEMENT_BRIBE_COST("inducementBribeCost"),
  INDUCEMENT_BRIBE_REDUCED_COST("inducementBribeReducedCost"),
  INDUCEMENT_CHEF_COST("inducementChefCost"),
  INDUCEMENT_CHEF_REDUCED_COST("inducementChefReducedCost"),
  INDUCEMENT_IGOR_COST("inducementIgorCost"),
  INDUCEMENT_APO_COST("inducementApoCost"),
  INDUCEMENT_WIZARD_COST("inducementWizardCost"),
  INDUCEMENT_MERCENARY_EXTRA_COST("inducementMercenaryExtraCost"),
  INDUCEMENT_MERCENARY_SKILL_COST("inducementMercenarySkillCost"),

  PITCH_URL("pitchUrl");
  
  private String fName;

  private GameOptionId(String pName) {
    fName = pName;
  }

  public String getName() {
    return fName;
  }
      
}
