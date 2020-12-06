package com.balancedbytes.games.ffb.client.ui;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.GameTitle;
import com.balancedbytes.games.ffb.client.UserInterface;

public class GameTitleUpdateTask implements Runnable {

	private FantasyFootballClient fClient;
	private GameTitle fGameTitle;

	public GameTitleUpdateTask(FantasyFootballClient pClient, GameTitle pGameTitle) {
		fClient = pClient;
		fGameTitle = pGameTitle;
	}

	public void run() {
		UserInterface userInterface = fClient.getUserInterface();
		userInterface.getGameTitle().update(fGameTitle);
		userInterface.refreshTitle();
	}

}
