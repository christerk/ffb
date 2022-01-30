package com.fumbbl.ffb.server;

import com.fumbbl.ffb.net.commands.ServerCommand;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ServerReplay {

	private ServerCommand[] fServerCommands;
	private long fGameId;
	private int fFromCommandNr;
	private final int fToCommandNr;
	private final Session fSession;
	private boolean fComplete;

	public ServerReplay(GameState gameState, int toCommandNr, Session session) {
		fToCommandNr = toCommandNr;
		fSession = session;
		if (gameState != null) {
			fGameId = gameState.getId();
			if (gameState.getGameLog() != null) {
				fServerCommands = gameState.getGameLog().getServerCommands();
			}
		}
		orderCommands();
	}

	public void setFromCommandNr(int pFromCommandNr) {
		fFromCommandNr = pFromCommandNr;
	}

	public int getFromCommandNr() {
		return fFromCommandNr;
	}

	public int getToCommandNr() {
		return fToCommandNr;
	}

	public ServerCommand[] getServerCommands() {
		return fServerCommands;
	}

	public long getGameId() {
		return fGameId;
	}

	public Session getSession() {
		return fSession;
	}

	public int size() {
		return fToCommandNr == 0 ? fServerCommands.length : fToCommandNr - 1;
	}

	public void setComplete(boolean pComplete) {
		fComplete = pComplete;
	}

	public boolean isComplete() {
		return fComplete;
	}

	private void orderCommands() {
		if (fServerCommands != null) {
			for (int i = 0; i < fServerCommands.length; i++) {
				fServerCommands[i].setCommandNr(i + 1);
			}
		}
	}

	public ServerCommand[] findRelevantCommandsInLog() {
		List<ServerCommand> replayCommands = new ArrayList<>();
		if (fServerCommands != null) {
			for (ServerCommand serverCommand : fServerCommands) {
				if ((serverCommand.getCommandNr() >= getFromCommandNr())
						&& ((getToCommandNr() == 0) || (serverCommand.getCommandNr() < getToCommandNr()))) {
					replayCommands.add(serverCommand);
				}
			}
		}
		return replayCommands.toArray(new ServerCommand[replayCommands.size()]);
	}

}
