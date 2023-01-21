package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUpdatePlayerMarkings;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.MarkerLoadingService;

public class ServerCommandHandlerUpdatePlayerMarkings extends ServerCommandHandler {


	protected ServerCommandHandlerUpdatePlayerMarkings(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public boolean handleCommand(ReceivedCommand receivedCommand) {
		ClientCommandUpdatePlayerMarkings commandUpdatePlayerMarkings = (ClientCommandUpdatePlayerMarkings) receivedCommand.getCommand();

		SessionManager sessionManager = getServer().getSessionManager();
		long gameId = sessionManager.getGameIdForSession(receivedCommand.getSession());
		GameState gameState = getServer().getGameCache().getGameStateById(gameId);
		ClientMode mode = sessionManager.getModeForSession(receivedCommand.getSession());
		boolean isHome = UtilServerSteps.checkCommandIsFromHomePlayer(gameState, receivedCommand);

		if (mode == ClientMode.PLAYER) {
			new MarkerLoadingService().loadMarker(gameState, receivedCommand.getSession(), isHome, commandUpdatePlayerMarkings.isAuto());
		}

		return true;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_UPDATE_PLAYER_MARKINGS;
	}
}
