package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

public class DialogConfirmWizardFriendlyFire extends DialogThreeWayChoice {

	public DialogConfirmWizardFriendlyFire(FantasyFootballClient pClient, SpecialEffect pWizardSpell) {
		super(pClient, "Confirm Wizard Spell", new String[]{
			StringTool.capitalize(pWizardSpell.getName()) + " will affect one or more of your own players.",
			"Do you want to cast it anyway?"
		}, null);
	}

	public DialogId getId() {
		return DialogId.YES_OR_NO_QUESTION;
	}
}