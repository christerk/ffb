package com.balancedbytes.games.ffb;


/**
 * 
 * @author Dominic Schabel
 * @author Kalimar
 */
public enum Sound implements IEnumWithId, IEnumWithName {

  BLOCK(1, "block", false),
  BLUNDER(2, "blunder", false),
  // BOO(3, "boo", true),
  BOUNCE(5, "bounce", false),
  CATCH(6, "catch", false),
  CHAINSAW(7, "chainsaw", false),
  // CHEER(8, "cheer", true),
  CLICK(9, "click", false),
  DING(10, "ding", false),
  DODGE(11, "dodge", false),
  DUH(12, "duh", false),
  EW(13, "ew", false),
  EXPLODE(14, "explode", false),
  FALL(15, "fall", false),
  FIREBALL(16, "fireball", false),
  FOUL(17, "foul", false),
  // GOTTA_HURT(18, "gottaHurt", true),
  HYPNO(19, "hypno", false),
  INJURY(20, "injury", false),
  KICK(21, "kick", false),
  KO(22, "ko", false),
  LIGHTNING(23, "lightning", false),
  METAL(24, "metal", false),
  NOMNOM(25, "nomnom", false),
  ORGAN(26, "organ", false),
  PICKUP(27, "pickup", false),
  QUESTION(28, "question", false),
  RIP(30, "rip", false),
  ROAR(31, "roar", false),
  ROOT(32, "root", false),
  SLURP(33, "slurp", false),
  STAB(34, "stab", false),
  STEP(35, "step", false),
  THROW(36, "throw", false),
  TOUCHDOWN(37, "touchdown", false),
  WHISTLE(38, "whistle", false),
  WOOOAAAH(39, "woooaaah", false),
  SPEC_AAH(40, "specAah", true),
  SPEC_BOO(41, "specBoo", true),
  SPEC_CHEER(42, "specCheer", true),
  SPEC_CLAP(43, "specClap", true),
  SPEC_CRICKETS(44, "specCrickets", true),
  SPEC_LAUGH(45, "specLaugh", true),
  SPEC_OOH(46, "specOoh", true),
  SPEC_SHOCK(47, "specShock", true),
  SPEC_STOMP(48, "specStomp", true);
  
  private int fId;
  private String fName;
  private boolean fSpectatorSound;

  private Sound(int pId, String pName, boolean pSpectatorSound) {
    fId = pId;
    fName = pName;
    fSpectatorSound = pSpectatorSound;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean isSpectatorSound() {
    return fSpectatorSound;
  }

}
