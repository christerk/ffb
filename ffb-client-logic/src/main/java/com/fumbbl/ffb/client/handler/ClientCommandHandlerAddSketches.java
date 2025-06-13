package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandAddSketches;

public class ClientCommandHandlerAddSketches extends AbstractClientCommandHandlerSketch<ServerCommandAddSketches> {

	protected ClientCommandHandlerAddSketches(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_ADD_SKETCHES;
	}

	@Override
	protected void updateSketchManager(ServerCommandAddSketches command) {
		ClientSketchManager sketchManager = getClient().getUserInterface().getSketchManager();
		command.getSketches().forEach(sketch -> sketchManager.add(command.getCoach(), sketch));
	}
}
