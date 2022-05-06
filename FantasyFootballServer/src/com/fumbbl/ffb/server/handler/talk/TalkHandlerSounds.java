package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TalkHandlerSounds extends TalkHandler {
	public TalkHandlerSounds() {
		super("/sounds", 0, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		List<String> soundNames = new ArrayList<>();
		for (SoundId soundId : SoundId.values()) {
			soundNames.add(soundId.getName());
		}
		Collections.sort(soundNames);
		String[] info = new String[soundNames.size() + 1];
		for (int i = 0; i < info.length; i++) {
			if (i > 0) {
				info[i] = soundNames.get(i - 1);
			} else {
				info[i] = "Available sounds:";
			}
		}
		server.getCommunication().sendTalk(session, null, info);
	}
}
