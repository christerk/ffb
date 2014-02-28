package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.IEnumWithName;




/**
 * 
 * @author Kalimar
 */
public enum GameOptionDefault implements IEnumWithName {
  
  OVERTIME("overtime", 0, null, "Game will go into overtime if there is a draw after 2nd half."), 
  TURNTIME("turntime", 240, null, "Turntime is $1 sec."),
  PETTY_CASH("pettyCash", 1, null, "Petty Cash is not available."),
  INDUCEMENTS("inducements", 1, null, "Inducements are not available."),
  CHECK_OWNERSHIP("checkOwnership", 1, null, "Team Ownership is not checked."),
  TEST_MODE("testMode", 0, null, "Game is in TEST mode. No result will be uploaded. See help for available test commands."),
  MAX_NR_OF_CARDS("maxNrOfCards", 5, null, "A maximum of $1 cards can be bought."),
  
  MAX_PLAYERS_ON_FIELD("maxPlayersOnField", 11, null, "A maximum of $1 players may be set up on the field."),
  MAX_PLAYERS_IN_WIDE_ZONE("maxPlayersInWideZone", 2, null, "A maximum of $1 players may be set up in a widezone."),
  MIN_PLAYERS_ON_LOS("minPlayersOnLos", 3, null, "A minimum of $1 players must be set up on the line of scrimmage."),

  CLAW_DOES_NOT_STACK("clawDoesNotStack", 0, null, "Claw does not stack with other skills that modify armour rolls."),
  FOUL_BONUS("foulBonus", 0, "foul", "+1 to armour roll for a foul."),
  FOUL_BONUS_OUTSIDE_TACKLEZONE("foulBonusOutsideTacklezone", 0, "foul", "+1 to armour roll for a foul, if fouler is not in an opposing tacklezone."),
  FREE_INDUCEMENT_CASH("freeInducementCash", 0, null, "Both coaches get $1 extra gold to buy inducements with."),
  FREE_CARD_CASH("freeCardCash", 0, null, "Both coaches get $1 extra gold to buy cards with."),
  PILING_ON_DOES_NOT_STACK("pilingOnDoesNotStack", 0, "pilingOn", "Piling On does not stack with other skills that modify armour- or injury-rolls."),
  PILING_ON_INJURY_ONLY("pilingOnInjuryOnly", 0, "pilingOn", "Piling On lets you re-roll injury-rolls only."),
  PILING_ON_ARMOR_ONLY("pilingOnArmorOnly", 0, "pilingOn", "Piling On lets you re-roll armour-rolls only."),
  PILING_ON_TO_KO_ON_DOUBLE("pilingOnToKoOnDouble", 0, "pilingOn", "Piling On player knocks himself out when rolling a double on armour or injury."),
  RIGHT_STUFF_CANCELS_TACKLE("rightStuffCancelsTackle", 0, null, "Right Stuff prevents Tackle from negating Dodge for Pow/Pushback."),
  SNEAKY_GIT_AS_FOUL_GUARD("sneakyGitAsFoulGuard", 0, "sneakyGit", "Sneaky Git works like Guard for fouling assists."),
  SNEAKY_GIT_BAN_TO_KO("sneakyGitBanToKo", 0, "sneakyGit", "Sneaky Git players that get banned are sent to the KO box instead."),
  STAND_FIRM_NO_DROP_ON_FAILED_DODGE("standFirmNoDropOnFailedDodge", 0, null, "Stand Firm players do not drop on a failed dodge roll but end their move instead."),
  SPIKED_BALL("spikedBall", 0, null, "A Spiked Ball is used for play. Any failed Pickup or Catch roll results in the player being stabbed.");
  
  private String fName;
  private int fDefaultValue;
  private String fGroup;
  private String fChangedMessage;

  private GameOptionDefault(String pName, int pDefaultValue, String pGroup, String pChangedMessage) {
    fName = pName;
    fGroup = pGroup;
    fDefaultValue = pDefaultValue;
    fChangedMessage = pChangedMessage;
  }

  public String getName() {
    return fName;
  }
  
  public int getDefaultValue() {
		return fDefaultValue;
	}

  public String getGroup() {
		return fGroup;
	}
  
  public String getChangedMessage() {
		return fChangedMessage;
	}
      
}
