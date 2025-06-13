package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandAddSketch;
import com.fumbbl.ffb.net.commands.ServerCommandAddSketches;
import com.fumbbl.ffb.server.FantasyFootballServer;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Collections;

public class ServerCommandHandlerAddSketch extends AbstractServerCommandHandlerSketch<ClientCommandAddSketch, ServerCommandAddSketches> {
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
	protected ServerCommandAddSketches createServerCommand(String coach, ClientCommandAddSketch command) {
		return new ServerCommandAddSketches(coach, Collections.singletonList(command.getSketch()));
	}
}
