package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandSketchSetLabel;

public class ClientCommandHandlerSketchSetLabel extends AbstractClientCommandHandlerSketch<ServerCommandSketchSetLabel> {

		protected ClientCommandHandlerSketchSetLabel(FantasyFootballClient pClient) {
				super(pClient);
		}

		@Override
		public NetCommandId getId() {
				return NetCommandId.SERVER_SKETCH_SET_LABEL;
		}

		@Override
		protected void updateSketchManager(ServerCommandSketchSetLabel command) {
			ClientSketchManager sketchManager = getClient().getUserInterface().getSketchManager();
			command.getSketchIds().forEach(id -> sketchManager.setLabel(command.getCoach(), id, command.getLabel()));
		}
}
