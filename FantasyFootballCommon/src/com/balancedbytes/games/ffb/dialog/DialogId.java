package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IEnumWithName;

/**
 * 
 * @author Kalimar
 */
public enum DialogId implements IEnumWithName {
  
  INFORMATION("information", false),
  YES_OR_NO_QUESTION("yesOrNoQuestion", true),
  GAME_COACH_PASSWORD("gameCoachPassword", false),
  TEAM_CHOICE("teamChoice", false),
  COIN_CHOICE("coinChoice", true),
  RE_ROLL("reRoll", true),
  SKILL_USE("skillUse", true),
  PROGRESS_BAR("progressBar", false),
  TEAM_SETUP("teamSetup", false),
  USE_APOTHECARY("useApothecary", true),
  RECEIVE_CHOICE("receiveChoice", true),
  FOLLOWUP_CHOICE("followupChoice", false),
  START_GAME("startGame", false),
  APOTHECARY_CHOICE("apothecaryChoice", true),
  TOUCHBACK("touchback", true),
  INTERCEPTION("interception", true),
  SETUP_ERROR("setupError", false),
  GAME_STATISTICS("gameStatistics", false),
  WINNINGS_RE_ROLL("winningsReRoll", true),
  GAME_CHOICE("gameChoice", false),
  KEY_BINDINGS("keyBindings", false),
  BLOCK_ROLL("blockRoll", true),
  PLAYER_CHOICE("playerChoice", true),
  DEFENDER_ACTION("defenderAction", true),
  JOIN("join", true),
  CONCEDE_GAME("concedeGame", false),
  ABOUT("about", false),
  END_TURN("endTurn", false),
  LEAVE_GAME("leaveGame", false),
  BRIBES("bribes", true),
  PILING_ON("pilingOn", true),
  BUY_INDUCEMENTS("buyInducements", true),
  TRANSFER_PETTY_CASH("transferPettyCash", true),
  SOUND_VOLUME("soundVolume", false),
  JOURNEYMEN("journeymen", true),
  KICKOFF_RESULT("kickoffResult", false),
  CHAT_COMMANDS("chatCommands", false),
  KICK_SKILL("kickSkill", true),
  USE_IGOR("useIgor", true),
  KICKOFF_RETURN("kickoffReturn", false),
  PETTY_CASH("pettyCash", true),
  WIZARD_SPELL("wizardSpell", false),  
  USE_INDUCEMENT("useInducement", false),
  PASS_BLOCK("passBlock", false),
  BUY_CARDS("buyCards", true),
  ARGUE_THE_CALL("argueTheCall", false);
  
  private String fName;
  private boolean fWaitingDialog;
  
  private DialogId(String pName, boolean pWaitingDialog) {
    fName = pName;
    fWaitingDialog = pWaitingDialog;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean isWaitingDialog() {
    return fWaitingDialog;
  }
    
}
