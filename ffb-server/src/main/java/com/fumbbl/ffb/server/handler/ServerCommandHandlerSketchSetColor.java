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
			sketchManager.setRgb(session, command.getSketchId(), command.getRbg());
	}

	@Override
	protected ServerCommandSketchSetColor createServerCommand(Session session, ClientCommandSketchSetColor command) {
		return new ServerCommandSketchSetColor(replaySessionManager.coach(session), command.getSketchId(), command.getRbg());
	}
}
