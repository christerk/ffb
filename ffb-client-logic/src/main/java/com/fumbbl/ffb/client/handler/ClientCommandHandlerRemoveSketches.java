package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandRemoveSketches;

public class ClientCommandHandlerRemoveSketches extends AbstractClientCommandHandlerSketch<ServerCommandRemoveSketches> {

		protected ClientCommandHandlerRemoveSketches(FantasyFootballClient pClient) {
				super(pClient);
		}

		@Override
		public NetCommandId getId() {
				return NetCommandId.SERVER_REMOVE_SKETCHES;
		}

		@Override
		protected void updateSketchManager(ServerCommandRemoveSketches command) {
			ClientSketchManager sketchManager = getClient().getUserInterface().getSketchManager();
			if (command.getIds() == null || command.getIds().isEmpty()) {
				sketchManager.removeAll(command.getCoach());
			} else {
				command.getIds().forEach(id -> sketchManager.remove(command.getCoach(), id));
			}
		}
}
