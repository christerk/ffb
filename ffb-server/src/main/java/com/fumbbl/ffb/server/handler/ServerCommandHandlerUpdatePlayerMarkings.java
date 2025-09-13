package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUpdatePlayerMarkings;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestLoadPlayerMarkings;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.util.MarkerLoadingService;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Collections;

public class ServerCommandHandlerUpdatePlayerMarkings extends ServerCommandHandler {


	protected ServerCommandHandlerUpdatePlayerMarkings(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public boolean handleCommand(ReceivedCommand receivedCommand) {
		ClientCommandUpdatePlayerMarkings commandUpdatePlayerMarkings = (ClientCommandUpdatePlayerMarkings) receivedCommand.getCommand();

		SessionManager sessionManager = getServer().getSessionManager();
		Session session = receivedCommand.getSession();
		long gameId = sessionManager.getGameIdForSession(session);
		GameState gameState = getServer().getGameCache().getGameStateById(gameId);
		if (gameState == null) {
			return false;
		}
		ClientMode mode = sessionManager.getModeForSession(session);
		boolean isHome = UtilServerSteps.checkCommandIsFromHomePlayer(gameState, receivedCommand);

		if (!commandUpdatePlayerMarkings.isAuto()) {
			sessionManager.removeAutoMarking(session);
		}

		if (mode == ClientMode.PLAYER) {
			new MarkerLoadingService().loadMarker(gameState, session, isHome, commandUpdatePlayerMarkings.isAuto(), commandUpdatePlayerMarkings.getSortMode());
		} else if (mode == ClientMode.SPECTATOR) {
			if (commandUpdatePlayerMarkings.isAuto()) {
				getServer().getRequestProcessor().add(new FumbblRequestLoadPlayerMarkings(gameState, session, commandUpdatePlayerMarkings.getSortMode()));
			} else {
				getServer().getCommunication().sendUpdateLocalPlayerMarkers(session, Collections.emptyList());
			}
		}

		return true;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_UPDATE_PLAYER_MARKINGS;
	}
}
