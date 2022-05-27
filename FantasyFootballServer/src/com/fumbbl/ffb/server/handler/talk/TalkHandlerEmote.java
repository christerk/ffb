package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashSet;

public class TalkHandlerEmote extends TalkHandler {
	public TalkHandlerEmote() {
		super(new HashSet<String>() {{
			add("/aah");
			add("/boo");
			add("/cheer");
			add("/clap");
			add("/crickets");
			add("/hurt");
			add("/laugh");
			add("/ooh");
			add("/shock");
			add("/stomp");
		}}, 0, TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		switch (commands[0]) {
			case "/aah":
				playSoundAfterCooldown(server, gameState, team.getCoach(), SoundId.SPEC_AAH);
				break;
			case "/boo":
				playSoundAfterCooldown(server, gameState, team.getCoach(), SoundId.SPEC_BOO);
				break;
			case "/cheer":
				playSoundAfterCooldown(server, gameState, team.getCoach(), SoundId.SPEC_CHEER);
				break;
			case "/clap":
				playSoundAfterCooldown(server, gameState, team.getCoach(), SoundId.SPEC_CLAP);
				break;
			case "/crickets":
				playSoundAfterCooldown(server, gameState, team.getCoach(), SoundId.SPEC_CRICKETS);
				break;
			case "/hurt":
				playSoundAfterCooldown(server, gameState, team.getCoach(), SoundId.SPEC_HURT);
				break;
			case "/laugh":
				playSoundAfterCooldown(server, gameState, team.getCoach(), SoundId.SPEC_LAUGH);
				break;
			case "/ooh":
				playSoundAfterCooldown(server, gameState, team.getCoach(), SoundId.SPEC_OOH);
				break;
			case "/shock":
				playSoundAfterCooldown(server, gameState, team.getCoach(), SoundId.SPEC_SHOCK);
				break;
			case "/stomp":
				playSoundAfterCooldown(server, gameState, team.getCoach(), SoundId.SPEC_STOMP);
				break;
			default:
				break;
		}
	}
}
