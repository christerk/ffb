package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandGameplaySettings;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Stores the gameplay settings of a playing coach in the game state, so steps can respect them.
 */
public class ServerCommandHandlerGameplaySettings extends ServerCommandHandler {

	private static final List<CommonProperty> ACCEPTED_SETTINGS = Arrays.asList(CommonProperty._SERVER_GAMEPLAY_SETTINGS);

	protected ServerCommandHandlerGameplaySettings(FantasyFootballServer server) {
		super(server);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_GAMEPLAY_SETTINGS;
	}

	public boolean handleCommand(ReceivedCommand receivedCommand) {
		ClientCommandGameplaySettings command = (ClientCommandGameplaySettings) receivedCommand.getCommand();
		Session session = receivedCommand.getSession();
		SessionManager sessionManager = getServer().getSessionManager();
		long gameId = sessionManager.getGameIdForSession(session);
		GameState gameState = getServer().getGameCache().getGameStateById(gameId);
		if (gameState == null || session == null) {
			return true;
		}

		boolean home = sessionManager.getSessionOfHomeCoach(gameId) == session;
		boolean away = sessionManager.getSessionOfAwayCoach(gameId) == session;
		if (!home && !away) {
			return true;
		}

		for (Map.Entry<CommonProperty, String> entry : command.getSettings().entrySet()) {
			if (ACCEPTED_SETTINGS.contains(entry.getKey())) {
				if (home) {
					gameState.setGameplaySetting(true, entry.getKey(), entry.getValue());
				}
				if (away) {
					gameState.setGameplaySetting(false, entry.getKey(), entry.getValue());
				}
			}
		}

		return true;
	}

}
