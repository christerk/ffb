package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSourceComposer;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsPropertiesParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class DialogReRollForTargetsPropertiesHandler extends AbstractReRollDialogHandler {

	private final ReRollSourceComposer reRollSourceComposer = new ReRollSourceComposer();

	public DialogReRollForTargetsPropertiesHandler(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public void showDialog() {
		Game game = getClient().getGame();
		DialogReRollForTargetsPropertiesParameter parameter =
			(DialogReRollForTargetsPropertiesParameter) game.getDialogParameter();
		if (parameter == null) {
			return;
		}
		Player<?> player = game.getPlayerById(parameter.getPlayerId());
		if (ClientMode.PLAYER == getClient().getMode() && game.getTeamHome().hasPlayer(player)) {
			setDialog(new DialogReRollForTargetsProperties(getClient(), parameter));
			getDialog().showDialog(this);
		} else {
			String actionName = parameter.getReRolledAction() == null ? null
				: parameter.getReRolledAction().getName(game.getRules()
					.<SkillFactory>getFactory(FactoryType.Factory.SKILL));
			showStatus("Re-roll", "Waiting to re-roll " + actionName + ".", StatusType.WAITING);
		}
	}

	@Override
	protected void reRollDialogClosed(IDialog dialog) {
		hideDialog();
		if (!testDialogHasId(dialog, DialogId.RE_ROLL_FOR_TARGETS_PROPERTIES)) {
			return;
		}
		DialogReRollForTargetsProperties reRollDialog = (DialogReRollForTargetsProperties) dialog;
		DialogReRollForTargetsPropertiesParameter parameter = reRollDialog.getDialogParameter();
		ReRollSource source = reRollDialog.getReRollSource();
		if (usesComposedSource(source)) {
			selectTeamReRoll(parameter, source, selectedSource -> sendChoice(reRollDialog, selectedSource));
		} else {
			sendChoice(reRollDialog, source);
		}
	}

	private boolean usesComposedSource(ReRollSource source) {
		return source == ReRollSources.PRO || source == ReRollSources.MASCOT
			|| reRollSourceComposer.compose(false, false, source) != null;
	}

	private void sendChoice(DialogReRollForTargetsProperties reRollDialog, ReRollSource source) {
		getClient().getCommunication().sendUseReRollForTarget(reRollDialog.getReRolledAction(), source,
			reRollDialog.getSelectedTarget());
	}
}
