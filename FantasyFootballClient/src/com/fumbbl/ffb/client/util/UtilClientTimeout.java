package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class UtilClientTimeout {

	public static void showTimeoutStatus(FantasyFootballClient pClient) {
		Game game = pClient.getGame();
		if (game.isTimeoutPossible()) {
			if (game.isHomePlaying()) {
				ClientData clientData = pClient.getClientData();
				clientData.setStatus("Timeout Possible", "Coach may force a Timeout on his/her opponent.", StatusType.REF);
			}
			UserInterface userInterface = pClient.getUserInterface();
			userInterface.getStatusReport().reportTimeout();
		}
	}

}
