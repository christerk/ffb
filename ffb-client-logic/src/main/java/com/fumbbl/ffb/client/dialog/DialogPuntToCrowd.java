package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

import java.awt.event.ActionListener;

public class DialogPuntToCrowd extends DialogThreeWayChoice implements ActionListener {

	public DialogPuntToCrowd(FantasyFootballClient pClient) {
		super(pClient, "Punt To Crowd", createMessages(), IIconProperty.GAME_BALL);
	}

	public DialogId getId() {
		return DialogId.PUNT_TO_CROWD;
	}

	private static String[] createMessages() {
		return new String[] {"Do you want to punt the ball into the crowd?", "This will cause a turnover"};
	}

}
