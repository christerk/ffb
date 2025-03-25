package com.fumbbl.ffb.client.net;

import java.util.TimerTask;

import com.fumbbl.ffb.client.FantasyFootballClient;

public class ClientPingTask extends TimerTask {

	private final FantasyFootballClient fClient;

	public ClientPingTask(FantasyFootballClient pClient) {
		fClient = pClient;
	}

	public void run() {
		if (getClient().getCommandEndpoint().isOpen()) {
			getClient().getCommunication().sendPing(System.currentTimeMillis());
		}
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

}
