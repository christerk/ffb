package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IClientProperty;
import com.fumbbl.ffb.client.dialog.DialogChangeList;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.model.ChangeList;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandUserSettings;

/**
 * @author Kalimar
 */
public class ClientCommandHandlerUserSettings extends ClientCommandHandler implements IDialogCloseListener {

	private DialogChangeList dialogChangeList;

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


		String lastFingerPrint = getClient().getProperty(IClientProperty.SETTING_LAST_CHANGE_LOG_FINGERPRINT);

		if (!ChangeList.INSTANCE.fingerPrint().equals(lastFingerPrint) && dialogChangeList == null) {
			dialogChangeList = new DialogChangeList(getClient());
			dialogChangeList.showDialog(this);
			getClient().setProperty(IClientProperty.SETTING_LAST_CHANGE_LOG_FINGERPRINT, ChangeList.INSTANCE.fingerPrint());
			String[] settingValues = new String[IClientProperty._SAVED_USER_SETTINGS.length];
			for (int i = 0; i < IClientProperty._SAVED_USER_SETTINGS.length; i++) {
				settingValues[i] = getClient().getProperty(IClientProperty._SAVED_USER_SETTINGS[i]);
			}
			getClient().getCommunication().sendUserSettings(IClientProperty._SAVED_USER_SETTINGS, settingValues);
		}

		return true;

	}

	@Override
	public void dialogClosed(IDialog pDialog) {
		if (dialogChangeList != null) {
			dialogChangeList.hideDialog();
		}
	}
}
