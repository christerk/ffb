package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IEnumWithId;
import com.balancedbytes.games.ffb.IEnumWithName;

/**
 * 
 * @author Kalimar
 */
public enum DialogId implements IEnumWithId, IEnumWithName {
  
  INFORMATION(1, "information", false),
  YES_OR_NO_QUESTION(2, "yesOrNoQuestion", true),
  GAME_COACH_PASSWORD(3, "gameCoachPassword", false),
  TEAM_CHOICE(4, "teamChoice", false),
  COIN_CHOICE(5, "coinChoice", true),
  RE_ROLL(6, "reRoll", true),
  SKILL_USE(7, "skillUse", true),
  PROGRESS_BAR(8, "progressBar", false),
  TEAM_SETUP(9, "teamSetup", false),
  USE_APOTHECARY(10, "useApothecary", true),
  RECEIVE_CHOICE(11, "receiveChoice", true),
  FOLLOWUP_CHOICE(12, "followupChoice", false),
  START_GAME(13, "startGame", false),
  APOTHECARY_CHOICE(14, "apothecaryChoice", true),
  TOUCHBACK(15, "touchback", true),
  INTERCEPTION(16, "interception", true),
  SETUP_ERROR(17, "setupError", false),
  GAME_STATISTICS(18, "gameStatistics", false),
  WINNINGS_RE_ROLL(19, "winningsReRoll", true),
  GAME_CHOICE(20, "gameChoice", false),
  KEY_BINDINGS(21, "keyBindings", false),
  BLOCK_ROLL(22, "blockRoll", true),
  PLAYER_CHOICE(23, "playerChoice", true),
  DEFENDER_ACTION(24, "defenderAction", true),
  JOIN(25, "join", true),
  CONCEDE_GAME(26, "concedeGame", false),
  ABOUT(27, "about", false),
  END_TURN(28, "endTurn", false),
  LEAVE_GAME(29, "leaveGame", false),
  BRIBES(30, "bribes", true),
  PILING_ON(31, "pilingOn", true),
  BUY_INDUCEMENTS(32, "buyInducements", true),
  TRANSFER_PETTY_CASH(33, "transferPettyCash", true),
  SOUND_VOLUME(34, "soundVolume", false),
  JOURNEYMEN(35, "journeymen", true),
  KICKOFF_RESULT(36, "kickoffResult", false),
  CHAT_COMMANDS(37, "chatCommands", false),
  KICK_SKILL(38, "kickSkill", true),
  USE_IGOR(39, "useIgor", true),
  KICKOFF_RETURN(40, "kickoffReturn", false),
  PETTY_CASH(41, "pettyCash", true),
  WIZARD_SPELL(42, "wizardSpell", false),  
  USE_INDUCEMENT(43, "useInducement", false),
  PASS_BLOCK(44, "passBlock", false),
  BUY_CARDS(45, "buyCards", true);
  
  private int fId;
  private String fName;
  private boolean fWaitingDialog;
  
  private DialogId(int pId, String pName, boolean pWaitingDialog) {
    fId = pId;
    fName = pName;
    fWaitingDialog = pWaitingDialog;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean isWaitingDialog() {
    return fWaitingDialog;
  }
    
}
