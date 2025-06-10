package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.model.sketch.SketchState;
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
		SketchState sketchState = new SketchState(sketchManager.getAllSketches());
		ModelChange modelChange = new ModelChange(ModelChangeId.SKETCH_UPDATE, null, sketchState);
		getClient().getGame().notifyObservers(modelChange);
		getClient().getUserInterface().getGameMenuBar().updateJoinedCoachesMenu();
		return false;
	}
}
