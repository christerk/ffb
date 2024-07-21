package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.ClientState;
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

	private final Map<NetCommandId, ClientCommandHandler> fCommandHandlerById;

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
	}

	public void handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
		long gameId = fClient.getGame() != null ? fClient.getGame().getId() : 0;
		if (pNetCommand != null) {
			ClientCommandHandler commandHandler = getCommandHandler(pNetCommand.getId());
			if (commandHandler != null) {
				boolean completed = commandHandler.handleNetCommand(pNetCommand, pMode);
				if (completed) {
					updateClientState(pNetCommand, false);
				} else {
					if (pMode == ClientCommandHandlerMode.PLAYING) {
						synchronized (this) {
							try {
								wait();
							} catch (InterruptedException ignored) {
							}
						}
					}
				}
			} else {
				updateClientState(pNetCommand, false);
			}
		} else {
			fClient.logDebug(gameId, "Received null command");

		}
	}

	public void updateClientState(NetCommand pNetCommand) {
		updateClientState(pNetCommand, true);
	}

	private void updateClientState(NetCommand pNetCommand, boolean pNotify) {
		ClientState clientState = getClient().updateClientState();
		if (clientState != null) {
			clientState.handleCommand(pNetCommand);
		}
		if (pNotify) {
			synchronized (this) {
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
