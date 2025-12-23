package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
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
            case RE_ROLL_PROPERTIES:
                return new DialogReRollPropertiesParameter();
            case RE_ROLL_FOR_TARGETS:
                return new DialogReRollForTargetsParameter();
            case SKILL_USE:
                return new DialogSkillUseParameter();
            case USE_APOTHECARY:
                return new DialogUseApothecaryParameter();
            case BLOCK_ROLL:
                return new DialogBlockRollParameter();
            case BLOCK_ROLL_PARTIAL_RE_ROLL:
                return new DialogBlockRollPartialReRollParameter();
            case BLOCK_ROLL_PROPERTIES:
                return new DialogBlockRollPropertiesParameter();
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
            case JOURNEYMEN:
                return new DialogJourneymenParameter();
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
            case SWARMING:
                return new DialogSwarmingPlayersParameter();
            case SWARMING_ERROR:
                return new DialogSwarmingErrorParameter();
            case BUY_CARDS_AND_INDUCEMENTS:
                return new DialogBuyCardsAndInducementsParameter();
            case SELECT_BLITZ_TARGET:
                return new DialogSelectBlitzTargetParameter();
            case SELECT_GAZE_TARGET:
                return new DialogSelectGazeTargetParameter();
            case RE_ROLL_BLOCK_FOR_TARGETS:
                return new DialogReRollBlockForTargetsParameter();
            case OPPONENT_BLOCK_SELECTION:
                return new DialogOpponentBlockSelectionParameter();
            case USE_APOTHECARIES:
                return new DialogUseApothecariesParameter();
            case USE_IGORS:
                return new DialogUseIgorsParameter();
            case PILE_DRIVER:
                return new DialogPileDriverParameter();
            case USE_CHAINSAW:
                return new DialogUseChainsawParameter();
            case INVALID_SOLID_DEFENCE:
                return new DialogInvalidSolidDefenceParameter();
            case SELECT_SKILL:
                return new DialogSelectSkillParameter();
            case BRIBERY_AND_CORRUPTION_RE_ROLL:
                return new DialogBriberyAndCorruptionParameter();
            case CONFIRM_END_ACTION:
                return new DialogConfirmEndActionParameter();
            case SELECT_WEATHER:
                return new DialogSelectWeatherParameter();
            case INFORMATION_OKAY:
                return new DialogInformationOkayParameter();
            case USE_MORTUARY_ASSISTANT:
                return new DialogUseMortuaryAssistantParameter();
            case USE_MORTUARY_ASSISTANTS:
                return new DialogUseMortuaryAssistantsParameter();
            case KICK_OFF_RESULT:
                return new DialogKickOffResultParameter();
            case BLOODLUST_ACTION:
                return new DialogBloodlustActionParameter();
            case PENALTY_SHOOTOUT:
                return new DialogPenaltyShootoutParameter();
            case BUY_PRAYERS_AND_INDUCEMENTS:
                return new DialogBuyPrayersAndInducementsParameter();
            default:
                return null;
        }
    }

    // JSON serialization

    public IDialogParameter forJsonValue(IFactorySource source, JsonValue pJsonValue) {
        if ((pJsonValue == null) || pJsonValue.isNull()) {
            return null;
        }
        JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
        DialogId dialogId = (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject);
        IDialogParameter dialogParameter = createDialogParameter(dialogId);
        if (dialogParameter != null) {
            dialogParameter.initFrom(source, pJsonValue);
        }
        return dialogParameter;
    }

}
