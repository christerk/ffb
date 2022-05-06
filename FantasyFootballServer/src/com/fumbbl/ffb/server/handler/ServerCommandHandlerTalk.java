package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandTalk;
import com.fumbbl.ffb.net.commands.ServerCommandTalk;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.util.StringTool;

/**
 * @author Kalimar
 */
public class ServerCommandHandlerTalk extends ServerCommandHandler {

	protected ServerCommandHandlerTalk(FantasyFootballServer server) {
		super(server);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_TALK;
	}

	public boolean handleCommand(ReceivedCommand receivedCommand) {

		ClientCommandTalk talkCommand = (ClientCommandTalk) receivedCommand.getCommand();
		SessionManager sessionManager = getServer().getSessionManager();
		ServerCommunication communication = getServer().getCommunication();
		long gameId = sessionManager.getGameIdForSession(receivedCommand.getSession());
		GameState gameState = getServer().getGameCache().getGameStateById(gameId);
		String talk = talkCommand.getTalk();

		if (talk != null) {

			String coach = sessionManager.getCoachForSession(receivedCommand.getSession());
			if ((gameState != null) && (sessionManager.getSessionOfHomeCoach(gameId) == receivedCommand.getSession())
				|| (sessionManager.getSessionOfAwayCoach(gameId) == receivedCommand.getSession())) {
					communication.sendPlayerTalk(gameState, coach, talk);

			} else {
				// Spectator
				if (talk.startsWith("/aah")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_AAH);
				} else if (talk.startsWith("/boo")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_BOO);
				} else if (talk.startsWith("/cheer")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_CHEER);
				} else if (talk.startsWith("/clap")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_CLAP);
				} else if (talk.startsWith("/crickets")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_CRICKETS);
				} else if (talk.startsWith("/hurt")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_HURT);
				} else if (talk.startsWith("/laugh")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_LAUGH);
				} else if (talk.startsWith("/ooh")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_OOH);
				} else if (talk.startsWith("/shock")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_SHOCK);
				} else if (talk.startsWith("/stomp")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_STOMP);
				} else {

					ServerCommandTalk.Mode mode = ServerCommandTalk.Mode.REGULAR;
					// Spectator chat
					if (sessionManager.isSessionAdmin(receivedCommand.getSession()) && ServerCommandTalk.Mode.STAFF.findIndicator(talk)) {
						mode = ServerCommandTalk.Mode.STAFF; // takes precedence
					} else if (sessionManager.isSessionDev(receivedCommand.getSession()) && ServerCommandTalk.Mode.DEV.findIndicator(talk)) {
						mode = ServerCommandTalk.Mode.DEV;
					}

					getServer().getCommunication().sendSpectatorTalk(gameState, coach, talk, mode);
				}
			}

		}

		return true;

	}

	private void playSoundAfterCooldown(GameState pGameState, String pCoach, SoundId pSound) {
		if ((pGameState != null) && (pCoach != null) && (pSound != null)) {
			if (StringTool.isProvided(getServer().getProperty(IServerProperty.SERVER_SPECTATOR_COOLDOWN))) {
				long spectatorCooldown = Long.parseLong(getServer().getProperty(IServerProperty.SERVER_SPECTATOR_COOLDOWN));
				long currentTime = System.currentTimeMillis();
				if (currentTime > (pGameState.getSpectatorCooldownTime(pCoach) + spectatorCooldown)) {
					getServer().getCommunication().sendSound(pGameState, pSound);
					pGameState.putSpectatorCooldownTime(pCoach, currentTime);
				}
			} else {
				getServer().getCommunication().sendSound(pGameState, pSound);
			}
		}
	}




}
