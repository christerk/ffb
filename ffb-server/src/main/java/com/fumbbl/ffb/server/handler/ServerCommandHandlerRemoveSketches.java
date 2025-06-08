package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandRemoveSketches;
import com.fumbbl.ffb.net.commands.ServerCommandRemoveSketches;
import com.fumbbl.ffb.server.FantasyFootballServer;
import org.eclipse.jetty.websocket.api.Session;

public class ServerCommandHandlerRemoveSketches extends AbstractServerCommandHandlerSketch<ClientCommandRemoveSketches, ServerCommandRemoveSketches> {
	protected ServerCommandHandlerRemoveSketches(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_REMOVE_SKETCHES;
	}

	@Override
	protected void updateSketchManager(Session session, ClientCommandRemoveSketches command) {
		if (command.getIds() == null || command.getIds().isEmpty()) {
			sketchManager.remove(session);
		} else {
			sketchManager.removeSketches(session, command.getIds());
		}
	}

	@Override
	protected ServerCommandRemoveSketches createServerCommand(String coach, ClientCommandRemoveSketches command) {
		return new ServerCommandRemoveSketches(coach, command.getIds());
	}
}
