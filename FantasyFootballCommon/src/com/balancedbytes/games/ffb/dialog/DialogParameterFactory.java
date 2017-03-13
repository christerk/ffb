package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class DialogParameterFactory {
  
  public IDialogParameter createDialogParameter(DialogId pDialogId) {
    if (pDialogId == null) {
      return null;
    }
    switch (pDialogId) {
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
      case ARGUE_THE_CALL:
        return new DialogArgueTheCallParameter();
      default:
        return null;
    }
  }

  // JSON serialization
  
  public IDialogParameter forJsonValue(JsonValue pJsonValue) {
    if ((pJsonValue == null) || pJsonValue.isNull()) {
      return null;
    }
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    DialogId dialogId = (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject);
    IDialogParameter dialogParameter = createDialogParameter(dialogId);
    if (dialogParameter != null) {
      dialogParameter.initFrom(pJsonValue);
    }
    return dialogParameter;
  }

}
