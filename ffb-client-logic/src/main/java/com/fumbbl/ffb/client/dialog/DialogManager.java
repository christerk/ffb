package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.inducements.DialogBuyCardsAndInducementsHandler;
import com.fumbbl.ffb.client.dialog.inducements.DialogBuyCardsHandler;
import com.fumbbl.ffb.client.dialog.inducements.DialogBuyInducementsHandler;
import com.fumbbl.ffb.client.dialog.inducements.DialogBuyPrayersAndInducementsHandler;
import com.fumbbl.ffb.client.dialog.inducements.DialogUseInducementHandler;
import com.fumbbl.ffb.model.Game;

/**
 * @author Kalimar
 */
public class DialogManager {

	private final FantasyFootballClient fClient;

	private DialogHandler fDialogHandler;
	private IDialogParameter fShownDialogParameter;

	public DialogManager(FantasyFootballClient pClient) {
		fClient = pClient;
	}

	public void updateDialog() {
		Game game = getClient().getGame();
		if ((getShownDialogParameter() != null) && (getShownDialogParameter() == game.getDialogParameter())) {
			if (getDialogHandler() != null) {
				getDialogHandler().updateDialog();
			}
		} else {
			if (getDialogHandler() != null) {
				getDialogHandler().hideDialog();
			}
			setShownDialogParameter(game.getDialogParameter());
			setDialogHandler(null);
			if (game.getDialogParameter() != null) {
				switch (game.getDialogParameter().getId()) {
					case RE_ROLL:
						setDialogHandler(new DialogReRollHandler(getClient()));
						break;
					case RE_ROLL_PROPERTIES:
						setDialogHandler(new DialogReRollPropertiesHandler(getClient()));
						break;
					case SKILL_USE:
						setDialogHandler(new DialogSkillUseHandler(getClient()));
						break;
					case USE_APOTHECARY:
						setDialogHandler(new DialogUseApothecaryHandler(getClient()));
						break;
					case APOTHECARY_CHOICE:
						setDialogHandler(new DialogApothecaryChoiceHandler(getClient()));
						break;
					case COIN_CHOICE:
						setDialogHandler(new DialogCoinChoiceHandler(getClient()));
						break;
					case INTERCEPTION:
						setDialogHandler(new DialogInterceptionHandler(getClient()));
						break;
					case RECEIVE_CHOICE:
						setDialogHandler(new DialogReceiveChoiceHandler(getClient()));
						break;
					case FOLLOWUP_CHOICE:
						setDialogHandler(new DialogFollowupChoiceHandler(getClient()));
						break;
					case TOUCHBACK:
						setDialogHandler(new DialogTouchbackHandler(getClient()));
						break;
					case SETUP_ERROR:
						setDialogHandler(new DialogSetupErrorHandler(getClient()));
						break;
					case START_GAME:
						setDialogHandler(new DialogStartGameHandler(getClient()));
						break;
					case TEAM_SETUP:
						setDialogHandler(new DialogTeamSetupHandler(getClient()));
						break;
					case WINNINGS_RE_ROLL:
						setDialogHandler(new DialogWinningsReRollHandler(getClient()));
						break;
					case BLOCK_ROLL:
						setDialogHandler(new DialogBlockRollHandler(getClient()));
						break;
					case BLOCK_ROLL_PARTIAL_RE_ROLL:
						setDialogHandler(new DialogBlockRollPartialReRollHandler(getClient()));
						break;
					case BLOCK_ROLL_PROPERTIES:
						setDialogHandler(new DialogBlockRollPropertiesHandler(getClient()));
						break;
					case PLAYER_CHOICE:
						setDialogHandler(new DialogPlayerChoiceHandler(getClient()));
						break;
					case DEFENDER_ACTION:
						setDialogHandler(new DialogDefenderActionHandler(getClient()));
						break;
					case JOIN:
						setDialogHandler(new DialogJoinHandler(getClient()));
						break;
					case CONCEDE_GAME:
						setDialogHandler(new DialogGameConcessionHandler(getClient()));
						break;
					case GAME_STATISTICS:
						setDialogHandler(new DialogGameStatisticsHandler(getClient()));
						break;
					case PILING_ON:
						setDialogHandler(new DialogPilingOnHandler(getClient()));
						break;
					case BRIBES:
						setDialogHandler(new DialogBribesHandler(getClient()));
						break;
					case BUY_INDUCEMENTS:
						setDialogHandler(new DialogBuyInducementsHandler(getClient()));
						break;
					case BUY_CARDS_AND_INDUCEMENTS:
						setDialogHandler(new DialogBuyCardsAndInducementsHandler(getClient()));
						break;
					case JOURNEYMEN:
						setDialogHandler(new DialogJourneymenHandler(getClient()));
						break;
					case KICK_SKILL:
						setDialogHandler(new DialogKickSkillHandler(getClient()));
						break;
					case USE_IGOR:
						setDialogHandler(new DialogUseIgorHandler(getClient()));
						break;
					case KICKOFF_RETURN:
						setDialogHandler(new DialogKickoffReturnHandler(getClient()));
						break;
					case PETTY_CASH:
						setDialogHandler(new DialogPettyCashHandler(getClient()));
						break;
					case WIZARD_SPELL:
						setDialogHandler(new DialogWizardSpellHandler(getClient()));
						break;
					case USE_INDUCEMENT:
						setDialogHandler(new DialogUseInducementHandler(getClient()));
						break;
					case PASS_BLOCK:
						setDialogHandler(new DialogPassBlockHandler(getClient()));
						break;
					case BUY_CARDS:
						setDialogHandler(new DialogBuyCardsHandler(getClient()));
						break;
					case ARGUE_THE_CALL:
						setDialogHandler(new DialogArgueTheCallHandler(getClient()));
						break;
					case SWARMING:
						setDialogHandler(new DialogSwarmingPlayersHandler(getClient()));
						break;
					case SWARMING_ERROR:
						setDialogHandler(new DialogSwarmingErrorParameterHandler(getClient()));
						break;
					case SELECT_BLITZ_TARGET:
						String blitzTargetPanelSetting = getClient().getProperty(CommonProperty.SETTING_BLITZ_TARGET_PANEL);
						if (IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_OFF.equals(blitzTargetPanelSetting)) {
							setDialogHandler(null);
						} else {
							setDialogHandler(new DialogSelectBlitzTargetHandler(getClient()));
						}
						break;
					case SELECT_GAZE_TARGET:
						String gazeTargetPanelSetting = getClient().getProperty(CommonProperty.SETTING_GAZE_TARGET_PANEL);
						if (IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_OFF.equals(gazeTargetPanelSetting)) {
							setDialogHandler(null);
						} else {
							setDialogHandler(new DialogSelectGazeTargetHandler(getClient()));
						}
						break;
					case RE_ROLL_FOR_TARGETS:
						setDialogHandler(new DialogReRollForTargetsHandler(getClient()));
						break;
					case RE_ROLL_BLOCK_FOR_TARGETS:
						setDialogHandler(new DialogReRollBlockForTargetsHandler(getClient()));
						break;
					case OPPONENT_BLOCK_SELECTION:
						setDialogHandler(new DialogOpponentBlockSelectionHandler(getClient()));
						break;
					case USE_APOTHECARIES:
						setDialogHandler(new DialogUseApothecariesHandler(getClient()));
						break;
					case USE_IGORS:
						setDialogHandler(new DialogUseIgorsHandler(getClient()));
						break;
					case PILE_DRIVER:
						setDialogHandler(new DialogPileDriverHandler(getClient()));
						break;
					case USE_CHAINSAW:
						setDialogHandler(new DialogUseChainsawHandler(getClient()));
						break;
					case INVALID_SOLID_DEFENCE:
						setDialogHandler(new DialogInvalidSolidDefenceHandler(getClient()));
						break;
					case SELECT_SKILL:
						setDialogHandler(new DialogSelectSkillHandler(getClient()));
						break;
					case BRIBERY_AND_CORRUPTION_RE_ROLL:
						setDialogHandler(new DialogBriberyAndCorruptionHandler(getClient()));
						break;
					case CONFIRM_END_ACTION:
						setDialogHandler(new DialogConfirmEndActionHandler(getClient()));
						break;
					case INFORMATION_OKAY:
						setDialogHandler(new DialogInformationOkayHandler(getClient()));
						break;
					case SELECT_WEATHER:
						setDialogHandler(new DialogSelectWeatherHandler(getClient()));
						break;
					case USE_MORTUARY_ASSISTANT:
						setDialogHandler(new DialogUseMortuaryAssistantHandler(getClient()));
						break;
					case USE_MORTUARY_ASSISTANTS:
						setDialogHandler(new DialogUseMortuaryAssistantsHandler(getClient()));
						break;
					case KICK_OFF_RESULT:
						setDialogHandler(new DialogKickOffResultHandler(getClient()));
						break;
					case BLOODLUST_ACTION:
						setDialogHandler(new DialogBloodlustActionHandler(getClient()));
						break;
					case PENALTY_SHOOTOUT:
						setDialogHandler(new DialogPenaltyShootoutHandler(getClient()));
						break;
					case BUY_PRAYERS_AND_INDUCEMENTS:
						setDialogHandler(new DialogBuyPrayersAndInducementsHandler(getClient()));
						break;
					case OPPONENT_BLOCK_SELECTION_PROPERTIES:
						setDialogHandler(new DialogOpponentBlockSelectionPropertiesHandler(getClient()));
						break;
					case RE_ROLL_BLOCK_FOR_TARGETS_PROPERTIES:
						setDialogHandler(new DialogReRollBlockForTargetsPropertiesHandler(getClient()));
						break;
					default:
						break;
				}
				if (getDialogHandler() != null) {
					getDialogHandler().showDialog();
				}
			}
		}
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public DialogHandler getDialogHandler() {
		return fDialogHandler;
	}

	private void setDialogHandler(DialogHandler pDialogHandler) {
		fDialogHandler = pDialogHandler;
	}

	public boolean isDialogHidden() {
		return ((getDialogHandler() == null) || (getDialogHandler().getDialog() == null)
			|| !getDialogHandler().getDialog().isVisible());
	}

	public boolean isEndTurnAllowed() {
		return (isDialogHidden() || getDialogHandler().isEndTurnAllowedWhileDialogVisible());
	}

	public IDialogParameter getShownDialogParameter() {
		return fShownDialogParameter;
	}

	public void setShownDialogParameter(IDialogParameter pShownDialogParameter) {
		fShownDialogParameter = pShownDialogParameter;
	}

}
