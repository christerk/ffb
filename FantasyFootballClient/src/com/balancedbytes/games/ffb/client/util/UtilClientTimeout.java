package com.balancedbytes.games.ffb.client.util;

import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.ClientData;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.model.Game;

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
