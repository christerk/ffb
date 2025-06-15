package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandClearSketches;

public class ClientCommandHandlerClearSketches extends AbstractClientCommandHandlerSketch<ServerCommandClearSketches> {

		protected ClientCommandHandlerClearSketches(FantasyFootballClient pClient) {
				super(pClient);
		}

		@Override
		public NetCommandId getId() {
				return NetCommandId.SERVER_CLEAR_SKETCHES;
		}

		@Override
		protected void updateSketchManager(ServerCommandClearSketches command) {
				getClient().getUserInterface().getSketchManager().clearAll();
		}
}
