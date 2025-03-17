package com.fumbbl.ffb.server;

import com.fumbbl.ffb.net.commands.ClientCommandReplayStatus;
import com.fumbbl.ffb.server.net.ReceivedCommand;

public class ReplayState {
	private String name;
	private int commandNr, speed;
	private boolean running, forward;


	public String getName() {
		return name;
	}

	public void handleCommand(ReceivedCommand command) {
		switch (command.getId()) {
			case CLIENT_REPLAY_STATUS:
				ClientCommandReplayStatus statusCommand = (ClientCommandReplayStatus) command.getCommand();
				this.commandNr = statusCommand.getCommandNr();
				this.speed = statusCommand.getSpeed();
				this.running = statusCommand.isRunning();
				this.running = statusCommand.isForward();
				break;
			default:
				break;
		}
	}

	public int getCommandNr() {
		return commandNr;
	}

	public int getSpeed() {
		return speed;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isForward() {
		return forward;
	}
}
