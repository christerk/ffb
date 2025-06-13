package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.model.sketch.SketchState;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.commands.ServerCommand;

public abstract class AbstractClientCommandHandlerSketch<S extends ServerCommand> extends ClientCommandHandler {

	protected AbstractClientCommandHandlerSketch(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public final boolean handleNetCommand(NetCommand command, ClientCommandHandlerMode pMode) {
		//noinspection unchecked
		updateSketchManager((S) command);
		ClientSketchManager sketchManager = getClient().getUserInterface().getSketchManager();
		SketchState sketchState = new SketchState(sketchManager.getAllSketches());
		getClient().getGame().notifyObservers(new ModelChange(ModelChangeId.SKETCH_UPDATE, null, sketchState));
		return true;
	}

	protected abstract void updateSketchManager(S command);
}
