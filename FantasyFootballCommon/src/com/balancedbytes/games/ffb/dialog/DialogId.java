package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
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
  
  public IDialogParameter createDialogParameter() {
    switch (this) {
      case APOTHECARY_CHOICE:
        return new DialogApothecaryChoiceParameter();
      case RECEIVE_CHOICE:
        return new DialogReceiveChoiceParameter();
      case RE_ROLL:
        return new DialogReRollParameter();
      case SKILL_USE:
        return new DialogSkillUseParameter();
      case USE_APOTHECARY:
        return new DialogUseApothecaryParameter();
      case BLOCK_ROLL:
        return new DialogBlockRollParameter();
      case PLAYER_CHOICE:
        return new DialogPlayerChoiceParameter();
      case INTERCEPTION:
        return new DialogInterceptionParameter();
      case WINNINGS_RE_ROLL:
        return new DialogWinningsReRollParameter();
      case BRIBES:
        return new DialogBribesParameter();
      case GAME_STATISTICS:
        return new DialogGameStatisticsParameter();
      case JOIN:
        return new DialogJoinParameter();
      case START_GAME:
        return new DialogStartGameParameter();
      case TEAM_SETUP:
        return new DialogTeamSetupParameter();
      case SETUP_ERROR:
        return new DialogSetupErrorParameter();
      case TOUCHBACK:
        return new DialogTouchbackParameter();
      case DEFENDER_ACTION:
        return new DialogDefenderActionParameter();
      case COIN_CHOICE:
        return new DialogCoinChoiceParameter();
      case FOLLOWUP_CHOICE:
        return new DialogFollowupChoiceParameter();
      case CONCEDE_GAME:
        return new DialogConcedeGameParameter();
      case PILING_ON:
        return new DialogPilingOnParameter();
      case BUY_INDUCEMENTS:
        return new DialogBuyInducementsParameter();
      case TRANSFER_PETTY_CASH:
        return new DialogTransferPettyCashParameter();
      case JOURNEYMEN:
        return new DialogJourneymenParameter();
      case KICKOFF_RESULT:
        return new DialogKickoffResultParameter();
      case KICK_SKILL:
        return new DialogKickSkillParameter();
      case USE_IGOR:
        return new DialogUseIgorParameter();
      case KICKOFF_RETURN:
        return new DialogKickoffReturnParameter();
      case PETTY_CASH:
        return new DialogPettyCashParameter();
      case WIZARD_SPELL:
        return new DialogWizardSpellParameter();
      case USE_INDUCEMENT:
      	return new DialogUseInducementParameter();
      case PASS_BLOCK:
      	return new DialogPassBlockParameter();
      case BUY_CARDS:
      	return new DialogBuyCardsParameter();
    	default:
    		break;
    }
    return null;
  }
  
}
