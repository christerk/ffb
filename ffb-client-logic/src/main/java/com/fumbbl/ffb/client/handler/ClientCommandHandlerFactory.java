package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class ClientCommandHandlerFactory {

	private final FantasyFootballClient fClient;

	private static final long ANIMATION_TIMEOUT_IN_MILLIS = 30000;

	private final Map<NetCommandId, ClientCommandHandler> fCommandHandlerById;

	private boolean animationRunning;

	public ClientCommandHandlerFactory(FantasyFootballClient pClient) {
		fClient = pClient;
		fCommandHandlerById = new HashMap<>();
		register(new ClientCommandHandlerJoin(getClient()));
		register(new ClientCommandHandlerLeave(getClient()));
		register(new ClientCommandHandlerTalk(getClient()));
		register(new ClientCommandHandlerGameState(getClient()));
		register(new ClientCommandHandlerSound(getClient()));
		register(new ClientCommandHandlerUserSettings(getClient()));
		register(new ClientCommandHandlerAdminMessage(getClient()));
		register(new ClientCommandHandlerModelSync(getClient()));
		register(new ClientCommandHandlerSocketClosed(getClient()));
		register(new ClientCommandHandlerAddPlayer(getClient()));
		register(new ClientCommandHandlerRemovePlayer(getClient()));
		register(new ClientCommandHandlerGameTime(getClient()));
		register(new ClientCommandHandlerZapPlayer(getClient()));
		register(new ClientCommandHandlerUnzapPlayer(getClient()));
		register(new ClientCommandHandlerUpdateLocalPlayerMarkers(getClient()));
		register(new ClientCommandHandlerAddSketches(getClient()));
		register(new ClientCommandHandlerRemoveSketches(getClient()));
		register(new ClientCommandHandlerClearSketches(getClient()));
		register(new ClientCommandHandlerSketchAddCoordinate(getClient()));
		register(new ClientCommandHandlerSketchSetColor(getClient()));
		register(new ClientCommandHandlerSketchSetLabel(getClient()));
		register(new ClientCommandHandlerSetPreventSketching(getClient()));
	}

	public void handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
		long gameId = fClient.getGame() != null ? fClient.getGame().getId() : 0;
		if (pNetCommand != null) {
			ClientCommandHandler commandHandler = getCommandHandler(pNetCommand.getId());
			if (commandHandler != null) {
				boolean playing = (pMode == ClientCommandHandlerMode.PLAYING);
				if (playing) {
					// arm the guard before the handler starts, otherwise a fast animation may signal completion
					// before this thread starts waiting
					synchronized (this) {
						animationRunning = true;
					}
				}
				boolean completed = commandHandler.handleNetCommand(pNetCommand, pMode);
				if (completed) {
					updateClientState(pNetCommand, false);
				} else {
					if (playing) {
						awaitAnimation();
					}
				}
			} else {
				updateClientState(pNetCommand, false);
			}
		} else {
			fClient.logDebug(gameId, "Received null command");

		}
	}

	/**
	 * Waits until the running animation signals completion. Uses a guard and a timeout, so a notification that is
	 * missed because the event dispatch thread was busy cannot block command processing forever.
	 */
	private void awaitAnimation() {
		synchronized (this) {
			long deadline = System.currentTimeMillis() + ANIMATION_TIMEOUT_IN_MILLIS;
			while (animationRunning) {
				long remaining = deadline - System.currentTimeMillis();
				if (remaining <= 0) {
					fClient.logDebug(0, "Timed out waiting for animation to finish");
					animationRunning = false;
					break;
				}
				try {
					wait(remaining);
				} catch (InterruptedException interrupted) {
					Thread.currentThread().interrupt();
					animationRunning = false;
				}
			}
		}
	}

	public void updateClientState(NetCommand pNetCommand) {
		updateClientState(pNetCommand, true);
	}

	private void updateClientState(NetCommand pNetCommand, boolean pNotify) {
		ClientState<? extends LogicModule, ? extends FantasyFootballClient> clientState = getClient().updateClientState();
		if (clientState != null) {
			clientState.handleCommand(pNetCommand);
		}
		if (pNotify) {
			synchronized (this) {
				animationRunning = false;
				notifyAll();
			}
		}
	}

	public ClientCommandHandler getCommandHandler(NetCommandId pType) {
		return fCommandHandlerById.get(pType);
	}

	private void register(ClientCommandHandler pCommandHandler) {
		fCommandHandlerById.put(pCommandHandler.getId(), pCommandHandler);
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

}
