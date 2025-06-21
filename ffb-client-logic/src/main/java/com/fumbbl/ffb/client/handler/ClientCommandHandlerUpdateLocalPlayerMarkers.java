package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandUpdateLocalPlayerMarkers;

public class ClientCommandHandlerUpdateLocalPlayerMarkers extends ClientCommandHandler {
	protected ClientCommandHandlerUpdateLocalPlayerMarkers(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_UPDATE_LOCAL_PLAYER_MARKERS;
	}

	@Override
	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {
		ServerCommandUpdateLocalPlayerMarkers commandUpdateLocalPlayerMarkers = (ServerCommandUpdateLocalPlayerMarkers) pNetCommand;

		FieldModel fieldModel = getClient().getGame().getFieldModel();
		for (PlayerMarker marker : fieldModel.getPlayerMarkers()) {
			fieldModel.remove(marker);
			getClient().getGame().notifyObservers(ModelChangeId.FIELD_MODEL_REMOVE_PLAYER_MARKER, null, marker);
		}

		commandUpdateLocalPlayerMarkers.getMarkers().forEach(marker -> {
			fieldModel.add(marker);
			getClient().getGame().notifyObservers(ModelChangeId.FIELD_MODEL_ADD_PLAYER_MARKER, null, marker);
		});

		getClient().getUserInterface().refresh();
		return true;
	}
}
