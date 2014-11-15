package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum SendToBoxReason implements IEnumWithId, IEnumWithName {
  
  MNG(1, "mng", "is recovering from a Serious Injury"),
  FOUL_BAN(2, "foulBan", "was banned for fouling"),
  SECRET_WEAPON_BAN(3, "secretWeaponBan", "was banned for using a Secret Weapon"),
  FOULED(4, "fouled", "was fouled"),
  BLOCKED(5, "blocked", "was blocked"),
  CROWD_PUSHED(6, "crowdPushed", "got pushed into the crowd"),
  DODGE_FAIL(7, "dodgeFail", "failed a dodge"),
  GFI_FAIL(8, "gfiFail", "failed to go for it"),
  LEAP_FAIL(9, "leapFail", "failed a leap"),
  STABBED(10, "stabbed", "has been stabbed"),
  HIT_BY_ROCK(11, "hitByRock", "has been hit by a rock"),
  EATEN(12, "eaten", "has been eaten"),
  HIT_BY_THROWN_PLAYER(13, "hitByThrownPlayer", "has been hit by a thrown player"),
  LANDING_FAIL(14, "landingFail", "failed to land after being thrown"),
  PILED_ON(15, "piledOn", "was piled upon"),
  CHAINSAW(16, "chainsaw", "has been hit by a chainsaw"),
  BITTEN(17, "bitten", "was bitten by a team-mate"),
  NURGLES_ROT(18, "nurglesRot", "has been infected with Nurgle's Rot"),
  RAISED(19, "raised", "has been raised from the dead"),
  LIGHTNING(20, "lightning", "has been hit by a lightning bolt"),
  FIREBALL(21, "fireball", "has been hit by a fireball"),
  KO_ON_PILING_ON(22, "koOnPilingOn", "has been knocked out while Piling On"),
  BOMB(23, "bomb", "has been hit by a bomb"),
  BALL_AND_CHAIN(24, "ballAndChain", "has been hit by a ball and chain");
  
  private int fId;
  private String fName;
  private String fReason;
  
  private SendToBoxReason(int pId, String pName, String pReason) {
    fId = pId;
    fName = pName;
    fReason = pReason;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getReason() {
    return fReason;
  }

}
