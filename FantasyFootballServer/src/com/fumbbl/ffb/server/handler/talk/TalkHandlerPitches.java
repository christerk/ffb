package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TalkHandlerPitches extends TalkHandler {
	public TalkHandlerPitches() {
		super("/pitches", 0, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	public void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		List<String> pitchNames = new ArrayList<>();
		for (String property : server.getProperties()) {
			if (property.startsWith("pitch.")) {
				pitchNames.add(property.substring(6));
			}
		}
		Collections.sort(pitchNames);
		String[] info = new String[pitchNames.size() + 1];
		for (int i = 0; i < info.length; i++) {
			if (i > 0) {
				info[i] = pitchNames.get(i - 1);
			} else {
				info[i] = "Available pitches:";
			}
		}
		server.getCommunication().sendTalk(session, null, info);
	}
}
