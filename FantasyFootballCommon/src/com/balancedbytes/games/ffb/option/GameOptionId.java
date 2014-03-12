package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.IEnumWithName;





/**
 * 
 * @author Kalimar
 */
public enum GameOptionId implements IEnumWithName {
  
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

  CLAW_DOES_NOT_STACK("clawDoesNotStack"),
  FOUL_BONUS("foulBonus"),
  FOUL_BONUS_OUTSIDE_TACKLEZONE("foulBonusOutsideTacklezone"),
  FREE_INDUCEMENT_CASH("freeInducementCash"),
  FREE_CARD_CASH("freeCardCash"),
  PILING_ON_DOES_NOT_STACK("pilingOnDoesNotStack"),
  PILING_ON_INJURY_ONLY("pilingOnInjuryOnly"),
  PILING_ON_ARMOR_ONLY("pilingOnArmorOnly"),
  PILING_ON_TO_KO_ON_DOUBLE("pilingOnToKoOnDouble"),
  RIGHT_STUFF_CANCELS_TACKLE("rightStuffCancelsTackle"),
  SNEAKY_GIT_AS_FOUL_GUARD("sneakyGitAsFoulGuard"),
  SNEAKY_GIT_BAN_TO_KO("sneakyGitBanToKo"),
  STAND_FIRM_NO_DROP_ON_FAILED_DODGE("standFirmNoDropOnFailedDodge"),
  SPIKED_BALL("spikedBall"),

  PITCH_URL_BLIZZARD("pitchUrlBlizzard"),
  PITCH_URL_HEAT("pitchUrlHeat"),
  PITCH_URL_NICE("pitchUrlNice"),
  PITCH_URL_RAIN("pitchUrlRain"),
  PITCH_URL_SUNNY("pitchUrlSunny");
  
  private String fName;

  private GameOptionId(String pName) {
    fName = pName;
  }

  public String getName() {
    return fName;
  }
      
}
