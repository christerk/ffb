package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogInvalidSolidDefenceParameter;

public class DialogInvalidSolidDefenceHandler extends DialogHandler {
	public DialogInvalidSolidDefenceHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public void showDialog() {
		DialogInvalidSolidDefenceParameter parameter = (DialogInvalidSolidDefenceParameter) getClient().getGame().getDialogParameter();

		if ((ClientMode.PLAYER == getClient().getMode()) && getClient().getGame().getTeamHome().getId().equals(parameter.getTeamId())) {
			setDialog(new DialogInformation(getClient(), "Too many moved players",
				new String[]{"You moved " + parameter.getAmount() + " players rather than the allowed " + parameter.getLimit() + "."},
				DialogInformation.OK_DIALOG, IIconProperty.GAME_REF));
			getDialog().showDialog(this);
		}
	}

	@Override
	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}
}
