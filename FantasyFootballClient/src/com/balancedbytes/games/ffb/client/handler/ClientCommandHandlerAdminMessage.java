package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.dialog.DialogInformation;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.client.dialog.IDialogCloseListener;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandAdminMessage;

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
