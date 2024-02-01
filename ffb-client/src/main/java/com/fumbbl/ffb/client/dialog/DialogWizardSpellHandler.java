package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogWizardSpellParameter;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogWizardSpellHandler extends DialogHandler {

	public DialogWizardSpellHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogWizardSpellParameter parameter = (DialogWizardSpellParameter) game.getDialogParameter();
		if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().getId().equals(parameter.getTeamId())) {
			setDialog(new DialogWizardSpell(getClient(), parameter.getTeamId()));
			getDialog().showDialog(this);

		} else {
			showStatus("Wizard Spell", "Waiting for coach to select a spell.", StatusType.WAITING);
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if ((pDialog != null) && (pDialog.getId() == DialogId.WIZARD_SPELL)) {
			SpecialEffect wizardSpell = ((DialogWizardSpell) pDialog).getWizardSpell();
			if (wizardSpell != null) {
				getClient().getClientData().setWizardSpell(wizardSpell);
			} else {
				getClient().getCommunication().sendWizardSpell(null, null); // signal cancel
			}
		}
	}

}
