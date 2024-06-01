package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.handler.RedeployHandler;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerRedeploy extends TalkHandler {

	private final RedeployHandler redeployHandler = new RedeployHandler();

	public TalkHandlerRedeploy() {
		super("/redeploy", 0, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_SERVER, TalkRequirements.Privilege.DEV);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		String branch = null;
		if (commands.length > 1) {
			branch = commands[1];
		}

		redeployHandler.redeploy(server, branch);
	}


}
