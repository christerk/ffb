package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUserSettings;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbTransaction;
import com.fumbbl.ffb.server.db.delete.DbUserSettingsDeleteParameter;
import com.fumbbl.ffb.server.db.insert.DbUserSettingsInsertParameterList;
import com.fumbbl.ffb.server.net.ReceivedCommand;

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

		}

		return true;

	}

}
