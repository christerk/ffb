package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandTalk;
import com.fumbbl.ffb.net.commands.ServerCommandTalk;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.SessionMode;
import com.fumbbl.ffb.server.Talk;
import com.fumbbl.ffb.server.handler.talk.TalkHandler;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestUploadTalk;
import com.fumbbl.ffb.util.Scanner;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kalimar
 */
public class ServerCommandHandlerTalk extends ServerCommandHandler {

	private final Set<TalkHandler> handlers = new HashSet<>();

	protected ServerCommandHandlerTalk(FantasyFootballServer server) {
		super(server);
		handlers.addAll(new Scanner<>(TalkHandler.class).getSubclassInstances());
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_TALK;
	}

	public boolean handleCommand(ReceivedCommand receivedCommand) {

		ClientCommandTalk talkCommand = (ClientCommandTalk) receivedCommand.getCommand();
		String talk = talkCommand.getTalk();

		if (talk != null) {
			SessionManager sessionManager = getServer().getSessionManager();
			long gameId = sessionManager.getGameIdForSession(receivedCommand.getSession());
			GameState gameState = getServer().getGameCache().getGameStateById(gameId);
			String coach = sessionManager.getCoachForSession(receivedCommand.getSession());

			if (gameState == null) {
				return true;
			}

			SessionMode sessionMode = SessionMode.SPEC;

			if (sessionManager.getSessionOfHomeCoach(gameId) == receivedCommand.getSession()) {
				sessionMode = SessionMode.HOME;
			} else if (sessionManager.getSessionOfAwayCoach(gameId) == receivedCommand.getSession()) {
				sessionMode = SessionMode.AWAY;
			} else if (sessionManager.isSessionAdmin(receivedCommand.getSession())) {
				sessionMode = SessionMode.ADMIN;
			} else if (sessionManager.isSessionDev(receivedCommand.getSession())) {
				sessionMode = SessionMode.DEV;
			}

			if (getServer().getMode() == ServerMode.FUMBBL) {
				getServer().getRequestProcessor().add(new FumbblRequestUploadTalk(new Talk(gameId, coach, sessionMode, talkCommand.getTalk()), gameState));
			}

			if (handlers.stream().anyMatch(handler -> handler.handle(getServer(), talkCommand, receivedCommand.getSession()))) {
				return true;
			}

			ServerCommunication communication = getServer().getCommunication();

			// Spectator chat
			if (sessionMode == SessionMode.HOME || sessionMode == SessionMode.AWAY) {
				communication.sendPlayerTalk(gameState, coach, talk);
			} else {
				ServerCommandTalk.Mode mode = ServerCommandTalk.Mode.REGULAR;
				if (sessionMode == SessionMode.ADMIN && ServerCommandTalk.Mode.STAFF.findIndicator(talk)) {
					mode = ServerCommandTalk.Mode.STAFF; // takes precedence
				} else if (sessionMode == SessionMode.DEV && ServerCommandTalk.Mode.DEV.findIndicator(talk)) {
					mode = ServerCommandTalk.Mode.DEV;
				}
				getServer().getCommunication().sendSpectatorTalk(gameState, coach, talk, mode);
			}

		}

		return true;

	}


}
