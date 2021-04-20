package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandUserSettings;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandHandlerUserSettings extends ClientCommandHandler {

	protected ClientCommandHandlerUserSettings(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_USER_SETTINGS;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {

		if (pMode == ClientCommandHandlerMode.QUEUING) {
			return true;
		}

		ServerCommandUserSettings userSettingsCommand = (ServerCommandUserSettings) pNetCommand;

		String[] settingNames = userSettingsCommand.getUserSettingNames();
		for (String settingName : settingNames) {
			getClient().setProperty(settingName, userSettingsCommand.getUserSettingValue(settingName));
		}

		if (pMode == ClientCommandHandlerMode.PLAYING) {
			refreshGameMenuBar();
		}

		return true;

	}

}
