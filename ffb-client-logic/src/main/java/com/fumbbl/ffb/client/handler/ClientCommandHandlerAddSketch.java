package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandAddSketch;

public class ClientCommandHandlerAddSketch extends AbstractClientCommandHandlerSketch<ServerCommandAddSketch> {

		protected ClientCommandHandlerAddSketch(FantasyFootballClient pClient) {
				super(pClient);
		}

		@Override
		public NetCommandId getId() {
				return NetCommandId.SERVER_ADD_SKETCH;
		}

		@Override
		protected void updateSketchManager(ServerCommandAddSketch command) {
				getClient().getUserInterface().getSketchManager().add(command.getSketch());
		}
}
