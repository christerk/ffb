package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.DialogInformation;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandAdminMessage;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandHandlerAdminMessage extends ClientCommandHandler implements IDialogCloseListener {

	protected ClientCommandHandlerAdminMessage(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_ADMIN_MESSAGE;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {

		ServerCommandAdminMessage messageCommand = (ServerCommandAdminMessage) pNetCommand;

		DialogInformation messageDialog = new DialogInformation(getClient(), "Administrator Message",
				messageCommand.getMessages(), DialogInformation.OK_DIALOG, false);
		messageDialog.showDialog(this);

		return true;

	}

	public void dialogClosed(IDialog pDialog) {
		pDialog.hideDialog();
	}

}
