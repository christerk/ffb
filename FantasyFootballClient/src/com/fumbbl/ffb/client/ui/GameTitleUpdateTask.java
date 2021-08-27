package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.GameTitle;
import com.fumbbl.ffb.client.UserInterface;

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
