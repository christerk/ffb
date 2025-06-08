package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandAddSketch;
import com.fumbbl.ffb.net.commands.ServerCommandAddSketch;
import com.fumbbl.ffb.server.FantasyFootballServer;
import org.eclipse.jetty.websocket.api.Session;

public class ServerCommandHandlerAddSketch extends AbstractServerCommandHandlerSketch<ClientCommandAddSketch, ServerCommandAddSketch> {
	protected ServerCommandHandlerAddSketch(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_ADD_SKETCH;
	}

	@Override
	protected void updateSketchManager(Session session, ClientCommandAddSketch command) {
		sketchManager.addSketch(session, command.getSketch());
	}

	@Override
	protected ServerCommandAddSketch createServerCommand(String coach, ClientCommandAddSketch command) {
		return new ServerCommandAddSketch(coach, command.getSketch());
	}
}
