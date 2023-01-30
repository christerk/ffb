package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class ServerCommandHandlerFactory {

	private final Map<NetCommandId, ServerCommandHandler> fCommandHandlerById;

	public ServerCommandHandlerFactory(FantasyFootballServer server) {
		fCommandHandlerById = new HashMap<>();
		register(new ServerCommandHandlerCloseGame(server));
		register(new ServerCommandHandlerDeleteGame(server));
		register(new ServerCommandHandlerFumbblGameChecked(server));
		register(new ServerCommandHandlerFumbblTeamLoaded(server));
		register(new ServerCommandHandlerJoin(server));
		register(new ServerCommandHandlerJoinApproved(server));
		register(new ServerCommandHandlerPasswordChallenge(server));
		register(new ServerCommandHandlerReplay(server));
		register(new ServerCommandHandlerReplayLoaded(server));
		register(new ServerCommandHandlerRequestVersion(server));
		register(new ServerCommandHandlerScheduleGame(server));
		register(new ServerCommandHandlerSetMarker(server));
		register(new ServerCommandHandlerSocketClosed(server));
		register(new ServerCommandHandlerTalk(server));
		register(new ServerCommandHandlerUploadGame(server));
		register(new ServerCommandHandlerUserSettings(server));
		register(new ServerCommandHandlerCloseSession(server));
		register(new ServerCommandHandlerPing(server));
		register(new ServerCommandHandlerUpdatePlayerMarkings(server));
	}

	public void handleCommand(ReceivedCommand receivedCommand) {
		ServerCommandHandler commandHandler = getCommandHandler(receivedCommand.getId());
		if (commandHandler != null) {
			commandHandler.handleCommand(receivedCommand);
		}
	}

	public ServerCommandHandler getCommandHandler(NetCommandId commandId) {
		return fCommandHandlerById.get(commandId);
	}

	private void register(ServerCommandHandler commandHandler) {
		fCommandHandlerById.put(commandHandler.getId(), commandHandler);
	}

}
