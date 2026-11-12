package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUserSettings;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.db.DbTransaction;
import com.fumbbl.ffb.server.db.delete.DbUserSettingsDeleteParameter;
import com.fumbbl.ffb.server.db.insert.DbUserSettingsInsertParameterList;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerUserSettings extends ServerCommandHandler {

	protected ServerCommandHandlerUserSettings(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USER_SETTINGS;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {

		ClientCommandUserSettings userSettingsCommand = (ClientCommandUserSettings) pReceivedCommand.getCommand();
		String coach = getServer().getSessionManager().getCoachForSession(pReceivedCommand.getSession());

		if (coach != null) {

			DbTransaction dbTransaction = new DbTransaction();

			dbTransaction.add(new DbUserSettingsDeleteParameter(coach));

			DbUserSettingsInsertParameterList insertParameters = new DbUserSettingsInsertParameterList();
			CommonProperty[] settingNames = userSettingsCommand.getSettingNames();
			for (CommonProperty settingName : settingNames) {
				insertParameters.addParameter(coach, settingName, userSettingsCommand.getSettingValue(settingName));
			}
			dbTransaction.add(insertParameters);

			getServer().getDbUpdater().add(dbTransaction);

			updateGameplaySettings(pReceivedCommand.getSession(), userSettingsCommand);
		}

		return true;

	}

	private void updateGameplaySettings(Session session, ClientCommandUserSettings userSettingsCommand) {
		SessionManager sessionManager = getServer().getSessionManager();
		long gameId = sessionManager.getGameIdForSession(session);
		GameState gameState = getServer().getGameCache().getGameStateById(gameId);
		if (gameState == null) {
			return;
		}
		Map<CommonProperty, String> settings = new HashMap<>();
		for (CommonProperty settingName : userSettingsCommand.getSettingNames()) {
			settings.put(settingName, userSettingsCommand.getSettingValue(settingName));
		}
		// a coach playing against themselves controls both sides
		if (sessionManager.getSessionOfHomeCoach(gameId) == session) {
			gameState.setGameplaySettings(true, settings);
		}
		if (sessionManager.getSessionOfAwayCoach(gameId) == session) {
			gameState.setGameplaySettings(false, settings);
		}
	}

}
