package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerMessage extends TalkHandler {

	private static final String MESSAGE_COMMAND = "/message";

	public TalkHandlerMessage() {
		super("/message", 0, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_SERVER, TalkRequirements.Privilege.DEV);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		String message = String.join(" ", commands);
		if (message.length() > MESSAGE_COMMAND.length()) {
			server.getCommunication().sendAdminMessage(new String[]{message.substring(MESSAGE_COMMAND.length() + 1).trim()});
		}
	}
}
