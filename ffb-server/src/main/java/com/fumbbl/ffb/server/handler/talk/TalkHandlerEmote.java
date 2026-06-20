package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.ChatCommand;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

@SuppressWarnings("unused")
public class TalkHandlerEmote extends TalkHandler {
	public TalkHandlerEmote() {
		super(ChatCommand.effectsAsStrings(), 0, TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		String coach = server.getSessionManager().getCoachForSession(session);

		ChatCommand chatCommand = ChatCommand.fromCommand(commands[0]);
		if (chatCommand == null) {
			return;
		}

		playSoundAfterCooldown(server, gameState, coach, chatCommand.getSoundId());
	}
}
