package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;

/**
 * @author Kalimar
 */
public class DialogPenaltyShootout extends Dialog {

	public DialogPenaltyShootout(FantasyFootballClient pClient) {
		super(pClient, "Penalty Shootout", true);

		this.add(new JLabel(dimensionProvider(), "DIALOG"));
		pack();
		setLocationToCenter();
	}

	public DialogId getId() {
		return DialogId.PENALTY_SHOOTOUT;
	}


}
