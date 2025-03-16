package com.fumbbl.ffb.server;

import com.fumbbl.ffb.server.net.ReceivedCommand;

public class ReplayState {
	private String name;
	private int commandNr, speed;
	private boolean running, forward;


	public String getName() {
		return name;
	}

	public void handleCommand(ReceivedCommand command) {

	}
}
