package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandSetPreventSketching;

public class ClientCommandHandlerSetPreventSketching extends ClientCommandHandler{

	protected ClientCommandHandlerSetPreventSketching(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_SET_PREVENT_SKETCHING;
	}

	@Override
	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
		ClientSketchManager sketchManager = getClient().getUserInterface().getSketchManager();
		ServerCommandSetPreventSketching command = (ServerCommandSetPreventSketching) pNetCommand;
		if (command.isPreventSketching()) {
			sketchManager.preventedFromSketching(command.getCoach());
		} else {
			sketchManager.allowSketching(command.getCoach());
		}
		getClient().getUserInterface().getGameMenuBar().updateJoinedCoachesMenu();
		return false;
	}
}
