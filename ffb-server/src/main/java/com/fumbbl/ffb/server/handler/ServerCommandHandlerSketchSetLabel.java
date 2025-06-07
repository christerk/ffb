package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandSketchSetLabel;
import com.fumbbl.ffb.net.commands.ServerCommandSketchSetLabel;
import com.fumbbl.ffb.server.FantasyFootballServer;
import org.eclipse.jetty.websocket.api.Session;

public class ServerCommandHandlerSketchSetLabel extends AbstractServerCommandHandlerSketch<ClientCommandSketchSetLabel, ServerCommandSketchSetLabel> {
	protected ServerCommandHandlerSketchSetLabel(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_SKETCH_SET_LABEL;
	}

	@Override
	protected void updateSketchManager(Session session, ClientCommandSketchSetLabel command) {
		command.getSketchIds().forEach(id -> sketchManager.setLabel(session, id, command.getLabel()));
	}

	@Override
	protected ServerCommandSketchSetLabel createServerCommand(Session session, ClientCommandSketchSetLabel command) {
		return new ServerCommandSketchSetLabel(replaySessionManager.coach(session), command.getSketchIds(), command.getLabel());
	}
}
