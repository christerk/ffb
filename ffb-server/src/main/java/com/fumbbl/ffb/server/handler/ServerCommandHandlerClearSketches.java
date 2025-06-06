package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandClearSketches;
import com.fumbbl.ffb.net.commands.ServerCommandClearSketches;
import com.fumbbl.ffb.server.FantasyFootballServer;
import org.eclipse.jetty.websocket.api.Session;

public class ServerCommandHandlerClearSketches extends AbstractServerCommandHandlerSketch<ClientCommandClearSketches, ServerCommandClearSketches> {
	protected ServerCommandHandlerClearSketches(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_CLEAR_SKETCHES;
	}

	@Override
	protected void updateSketchManager(Session session, ClientCommandClearSketches command) {
			sketchManager.remove(session);
			replaySessionManager.otherSessions(session).forEach(sketchManager::remove);
	}

	@Override
	protected ServerCommandClearSketches createServerCommand(Session session, ClientCommandClearSketches command) {
		return new ServerCommandClearSketches();
	}
}
