package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TalkHandlerAnimations extends TalkHandler {
	public TalkHandlerAnimations() {
		super("/animations", 0, TalkRequirements.Client.PLAYER, TalkRequirements.Environment.TEST_GAME);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		List<String> animationNames = new ArrayList<>();
		for (AnimationType animationType : AnimationType.values()) {
			animationNames.add(animationType.getName());
		}
		Collections.sort(animationNames);
		String[] info = new String[animationNames.size() + 1];
		for (int i = 0; i < info.length; i++) {
			if (i > 0) {
				info[i] = animationNames.get(i - 1);
			} else {
				info[i] = "Available animations:";
			}
		}
		server.getCommunication().sendTalk(session, null, info);
	}

}
