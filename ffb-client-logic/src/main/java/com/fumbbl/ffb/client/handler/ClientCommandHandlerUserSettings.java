package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
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

		CommonProperty[] settingNames = userSettingsCommand.getUserSettingNames();
		for (CommonProperty settingName : settingNames) {
			getClient().setProperty(settingName, userSettingsCommand.getUserSettingValue(settingName));
		}

		getClient().updateLocalPropertiesStore();

		if (pMode == ClientCommandHandlerMode.PLAYING) {
			refreshGameMenuBar();
		}


		String lastFingerPrint = getClient().getProperty(CommonProperty.SETTING_LAST_CHANGE_LOG_FINGERPRINT);

		if (!ChangeList.INSTANCE.fingerPrint().equals(lastFingerPrint) && dialogChangeList == null) {
			dialogChangeList = new DialogChangeList(getClient());
			dialogChangeList.showDialog(this);
			getClient().setProperty(CommonProperty.SETTING_LAST_CHANGE_LOG_FINGERPRINT, ChangeList.INSTANCE.fingerPrint());
			getClient().saveUserSettings(false);
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
