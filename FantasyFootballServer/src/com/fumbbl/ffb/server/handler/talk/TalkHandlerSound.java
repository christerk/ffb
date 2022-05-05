package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.SoundIdFactory;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public class TalkHandlerSound extends TalkHandler {
	public TalkHandlerSound() {
		super("/sound", 1, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		if (commands.length > 1) {
			SoundId soundId = new SoundIdFactory().forName(commands[1]);
			if (soundId == null) {
				return;
			}
			server.getCommunication().sendPlayerTalk(gameState, null, "Playing sound " + soundId.getName());
			server.getCommunication().sendSound(gameState, soundId);
		}
	}
}
