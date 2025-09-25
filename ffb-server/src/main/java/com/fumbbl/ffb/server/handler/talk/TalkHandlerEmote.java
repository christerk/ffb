package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TalkConstants;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashSet;

public class TalkHandlerEmote extends TalkHandler {
	public TalkHandlerEmote() {
		super(new HashSet<>(TalkConstants.EMOTES), 0, TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		String coach = server.getSessionManager().getCoachForSession(session);

		switch (commands[0]) {
			case "/aah":
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_AAH);
				break;
			case "/boo":
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_BOO);
				break;
			case "/cheer":
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_CHEER);
				break;
			case "/clap":
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_CLAP);
				break;
			case "/crickets":
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_CRICKETS);
				break;
			case "/hurt":
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_HURT);
				break;
			case "/laugh":
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_LAUGH);
				break;
			case "/ooh":
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_OOH);
				break;
			case "/shock":
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_SHOCK);
				break;
			case "/stomp":
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_STOMP);
				break;
			default:
				break;
		}
	}
}
