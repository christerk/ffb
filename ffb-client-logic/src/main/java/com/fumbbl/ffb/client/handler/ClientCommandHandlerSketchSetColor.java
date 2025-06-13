package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandSketchSetColor;

public class ClientCommandHandlerSketchSetColor extends AbstractClientCommandHandlerSketch<ServerCommandSketchSetColor> {

		protected ClientCommandHandlerSketchSetColor(FantasyFootballClient pClient) {
				super(pClient);
		}

		@Override
		public NetCommandId getId() {
				return NetCommandId.SERVER_SKETCH_SET_COLOR;
		}

		@Override
		protected void updateSketchManager(ServerCommandSketchSetColor command) {
			ClientSketchManager sketchManager = getClient().getUserInterface().getSketchManager();
			command.getSketchIds().forEach(id -> sketchManager.setColor(command.getCoach(), id, command.getRbg()));
		}
}
