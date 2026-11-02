package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSourceComposer;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class DialogReRollForTargetsHandler extends AbstractReRollDialogHandler {

	private final ReRollSourceComposer reRollSourceComposer = new ReRollSourceComposer();

	public DialogReRollForTargetsHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogReRollForTargetsParameter dialogReRollParameter = (DialogReRollForTargetsParameter) game.getDialogParameter();

		if (dialogReRollParameter != null) {

			Player<?> player = game.getPlayerById(dialogReRollParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {
				setDialog(new DialogReRollForTargets(getClient(), dialogReRollParameter));
				getDialog().showDialog(this);

			} else {
				StringBuilder message = new StringBuilder();
				String reRolledActionName = (dialogReRollParameter.getReRolledAction() != null)
						? dialogReRollParameter.getReRolledAction().getName(game.getRules().<SkillFactory>getFactory(FactoryType.Factory.SKILL))
						: null;
				message.append("Waiting to re-roll ").append(reRolledActionName).append(".");
				showStatus("Re-roll", message.toString(), StatusType.WAITING);
			}
		}
	}

	@Override
	protected void reRollDialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.RE_ROLL_FOR_TARGETS)) {
			DialogReRollForTargets reRollDialog = (DialogReRollForTargets) pDialog;
			DialogReRollForTargetsParameter parameter = reRollDialog.getDialogParameter();
			ReRollSource source = reRollDialog.getReRollSource();
			if (parameter.usesExplicitTeamReRollSources() && usesComposedSource(source)) {
				selectTeamReRoll(parameter, source, selectedSource -> sendChoice(reRollDialog, selectedSource));
			} else {
				sendChoice(reRollDialog, source);
			}
		}
	}

	private boolean usesComposedSource(ReRollSource source) {
		return source == ReRollSources.PRO || source == ReRollSources.MASCOT
			|| reRollSourceComposer.compose(false, false, source) != null;
	}

	private void sendChoice(DialogReRollForTargets reRollDialog, ReRollSource source) {
		getClient().getCommunication().sendUseReRollForTarget(reRollDialog.getReRolledAction(), source,
			reRollDialog.getSelectedTarget());
	}
}
