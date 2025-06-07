package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandSketchAddCoordinate;

public class ClientCommandHandlerSketchAddCoordinate extends AbstractClientCommandHandlerSketch<ServerCommandSketchAddCoordinate> {

		protected ClientCommandHandlerSketchAddCoordinate(FantasyFootballClient pClient) {
				super(pClient);
		}

		@Override
		public NetCommandId getId() {
				return NetCommandId.SERVER_SKETCH_ADD_COORDINATE;
		}

		@Override
		protected void updateSketchManager(ServerCommandSketchAddCoordinate command) {
			ClientSketchManager sketchManager = getClient().getUserInterface().getSketchManager();
			sketchManager.add(command.getCoach(), command.getSketchId(), command.getCoordinate());
		}
}
