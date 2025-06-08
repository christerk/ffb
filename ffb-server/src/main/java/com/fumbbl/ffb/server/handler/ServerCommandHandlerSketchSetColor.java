package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandSketchSetColor;
import com.fumbbl.ffb.net.commands.ServerCommandSketchSetColor;
import com.fumbbl.ffb.server.FantasyFootballServer;
import org.eclipse.jetty.websocket.api.Session;

public class ServerCommandHandlerSketchSetColor extends AbstractServerCommandHandlerSketch<ClientCommandSketchSetColor, ServerCommandSketchSetColor> {
	protected ServerCommandHandlerSketchSetColor(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_SKETCH_SET_COLOR;
	}

	@Override
	protected void updateSketchManager(Session session, ClientCommandSketchSetColor command) {
		command.getSketchIds().forEach(id -> sketchManager.setRgb(session, id, command.getRbg()));
	}

	@Override
	protected ServerCommandSketchSetColor createServerCommand(String coach, ClientCommandSketchSetColor command) {
		return new ServerCommandSketchSetColor(coach, command.getSketchIds(), command.getRbg());
	}
}
