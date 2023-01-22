package com.fumbbl.ffb.server;

import com.fumbbl.ffb.net.commands.ServerCommand;
import com.fumbbl.ffb.net.commands.ServerCommandReplay;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestLoadPlayerMarkings;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kalimar
 */
public class ServerReplayer implements Runnable {

	private boolean fStopped;
	private final List<ServerReplay> fReplayQueue;
	private final FantasyFootballServer fServer;

	public ServerReplayer(FantasyFootballServer pServer) {
		fServer = pServer;
		fReplayQueue = new LinkedList<ServerReplay>();
	}

	public void add(ServerReplay pReplay) {
		synchronized (fReplayQueue) {
			fReplayQueue.add(pReplay);
			fReplayQueue.notify();
		}
	}

	public void run() {

		ServerReplay serverReplay = null;

		while (true) {

			try {

				synchronized (fReplayQueue) {
					try {
						while (fReplayQueue.isEmpty() && !fStopped) {
							fReplayQueue.wait();
						}
					} catch (InterruptedException e) {
						break;
					}
					if (fStopped) {
						break;
					}
					if (serverReplay == null) {
						serverReplay = fReplayQueue.remove(0);
					}
				}

				while (serverReplay != null) {

					serverReplay.setComplete(true);

					ServerCommandReplay replayCommand = new ServerCommandReplay();
					replayCommand.setTotalNrOfCommands(serverReplay.size());
					replayCommand.setLastCommand(true);

					ServerCommand[] serverCommands = serverReplay.findRelevantCommandsInLog();
					for (ServerCommand serverCommand : serverCommands) {
						replayCommand.add(serverCommand);
						if (replayCommand.getNrOfCommands() >= ServerCommandReplay.MAX_NR_OF_COMMANDS) {
							serverReplay.setComplete(false);
							replayCommand.setLastCommand(false);
							break;
						}
					}

					getServer().getCommunication().send(serverReplay.getSession(), replayCommand, false);
					if (getServer().getDebugLog().isLogging(IServerLogLevel.DEBUG)) {
						StringBuilder message = new StringBuilder().append("Replay commands ").append(replayCommand.getCommandNr());
						message.append(replayCommand.findLowestCommandNr()).append(" - ")
							.append(replayCommand.findHighestCommandNr());
						message.append(" of ").append(replayCommand.getTotalNrOfCommands()).append(" total.");
						getServer().getDebugLog().log(IServerLogLevel.DEBUG, serverReplay.getGameId(),
							DebugLog.COMMAND_SERVER_SPECTATOR, message.toString());
					}

					if (!serverReplay.isComplete()) {
						serverReplay.setFromCommandNr(replayCommand.findHighestCommandNr() + 1);
					} else {
						//TODO check setting
						getServer().getRequestProcessor().add(new FumbblRequestLoadPlayerMarkings(serverReplay.getGameState(), serverReplay.getSession()));
						serverReplay = null;
					}

				}

			} catch (Exception pException) {
				getServer().getDebugLog().log(serverReplay.getGameId(), pException);
			}

		}

	}

	public void stop() {
		fStopped = true;
		synchronized (fReplayQueue) {
			fReplayQueue.notifyAll();
		}
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

}
