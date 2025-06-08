package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandSketchAddCoordinate;
import com.fumbbl.ffb.net.commands.ServerCommandSketchAddCoordinate;
import com.fumbbl.ffb.server.FantasyFootballServer;
import org.eclipse.jetty.websocket.api.Session;

public class ServerCommandHandlerSketchAddCoordinate extends AbstractServerCommandHandlerSketch<ClientCommandSketchAddCoordinate, ServerCommandSketchAddCoordinate> {
	protected ServerCommandHandlerSketchAddCoordinate(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_SKETCH_ADD_COORDINATE;
	}

	@Override
	protected void updateSketchManager(Session session, ClientCommandSketchAddCoordinate command) {
			sketchManager.addPathCoordinate(session, command.getSketchId(), command.getCoordinate());
	}

	@Override
	protected ServerCommandSketchAddCoordinate createServerCommand(String coach, ClientCommandSketchAddCoordinate command) {
		return new ServerCommandSketchAddCoordinate(coach, command.getSketchId(), command.getCoordinate());
	}
}
