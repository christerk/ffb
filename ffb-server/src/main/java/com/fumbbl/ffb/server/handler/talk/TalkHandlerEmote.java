package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.SpecCommand;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

@SuppressWarnings("unused")
public class TalkHandlerEmote extends TalkHandler {
	public TalkHandlerEmote() {
		super(SpecCommand.effectsAsStrings(), 0, TalkRequirements.Client.SPEC, TalkRequirements.Environment.NONE);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {

		String coach = server.getSessionManager().getCoachForSession(session);

		SpecCommand specCommand = SpecCommand.fromCommand(commands[0]);
		if (specCommand == null) {
			return;
		}

		switch (specCommand) {
			case AAH:
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_AAH);
				break;
			case BOO:
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_BOO);
				break;
			case CHEER:
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_CHEER);
				break;
			case CLAP:
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_CLAP);
				break;
			case CRICKETS:
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_CRICKETS);
				break;
			case HURT:
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_HURT);
				break;
			case LAUGH:
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_LAUGH);
				break;
			case OOH:
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_OOH);
				break;
			case SHOCK:
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_SHOCK);
				break;
			case STOMP:
				playSoundAfterCooldown(server, gameState, coach, SoundId.SPEC_STOMP);
				break;
			default:
				break;
		}
	}
}
