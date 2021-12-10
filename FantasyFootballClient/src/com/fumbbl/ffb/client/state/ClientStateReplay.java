package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.ClientReplayer;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IProgressListener;
import com.fumbbl.ffb.client.dialog.DialogProgressBar;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.net.commands.ServerCommand;
import com.fumbbl.ffb.net.commands.ServerCommandReplay;
import com.fumbbl.ffb.net.commands.ServerCommandStatus;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class ClientStateReplay extends ClientState implements IDialogCloseListener, IProgressListener {

	private DialogProgressBar fDialogProgress;
	private List<ServerCommand> fReplayList;

	protected ClientStateReplay(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.REPLAY;
	}

	public void enterState() {
		super.enterState();
		setSelectable(true);
		setClickable(false);
		ClientReplayer replayer = getClient().getReplayer();
		if (ClientMode.REPLAY == getClient().getMode()) {
			replayer.start();
			getClient().getCommunication().sendReplay(getClient().getParameters().getGameId(), 0);
		} else {
			if (fReplayList == null) {
				fReplayList = new ArrayList<>();
				showProgressDialog();
				getClient().getCommunication().sendReplay(0, replayer.getFirstCommandNr());
			} else {
				replayer.positionOnLastCommand();
				replayer.getReplayControl().setActive(true);
			}
		}
	}

	public void handleCommand(NetCommand pNetCommand) {
		ClientReplayer replayer = getClient().getReplayer();
		switch (pNetCommand.getId()) {
		case SERVER_REPLAY:
			ServerCommandReplay replayCommand = (ServerCommandReplay) pNetCommand;
			initProgress(0, replayCommand.getTotalNrOfCommands());
			for (ServerCommand command : replayCommand.getReplayCommands()) {
				fReplayList.add(command);
				updateProgress(fReplayList.size(), "Received Step %d of %d.");
			}
			if (fReplayList.size() >= replayCommand.getTotalNrOfCommands()) {
				fDialogProgress.hideDialog();
				// signal server that we've received the full replay and the session can be
				// closed
				if (ClientMode.REPLAY == getClient().getMode()) {
					getClient().getCommunication().sendCloseSession();
				}
				ServerCommand[] replayCommands = fReplayList.toArray(new ServerCommand[fReplayList.size()]);
				fDialogProgress = new DialogProgressBar(getClient(), "Initializing Replay");
				fDialogProgress.showDialog(this);
				replayer.init(replayCommands, this);
				fDialogProgress.hideDialog();
				if (ClientMode.REPLAY == getClient().getMode()) {
					replayer.positionOnFirstCommand();
				} else {
					replayer.positionOnLastCommand();
				}
				replayer.getReplayControl().setActive(true);
			}
			break;
		case SERVER_GAME_STATE:
			if (ClientMode.REPLAY == getClient().getMode()) {
				fReplayList = new ArrayList<>();
				showProgressDialog();
			}
			break;
		case SERVER_STATUS:
			ServerCommandStatus statusCommand = (ServerCommandStatus) pNetCommand;
			if ((ServerStatus.REPLAY_UNAVAILABLE == statusCommand.getServerStatus())
					&& (ClientMode.REPLAY == getClient().getMode())) {
				getClient().getUserInterface().getStatusReport().reportStatus(statusCommand.getServerStatus());
				getClient().getCommunication().sendCloseSession();
			}
			break;
		default:
			break;
		}
	}

	public void dialogClosed(IDialog pDialog) {
	}

	public void updateProgress(int pProgress) {
		updateProgress(pProgress, "Initialized Frame %d of %d.");
	}

	private void updateProgress(int pProgress, String pFormat) {
		String message = String.format(pFormat, pProgress, fDialogProgress.getMaximum());
		fDialogProgress.updateProgress(pProgress, message);
	}

	public void initProgress(int pMinimum, int pMaximum) {
		fDialogProgress.setMinimum(pMinimum);
		fDialogProgress.setMaximum(pMaximum);
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = false;
		if ((ClientMode.SPECTATOR == getClient().getMode()) && (pActionKey == ActionKey.MENU_REPLAY)) {
			actionHandled = true;
			getClient().getReplayer().stop();
			getClient().updateClientState();
			getClient().getUserInterface().getGameMenuBar().refresh();
		}
		return actionHandled;
	}

	private void showProgressDialog() {
		fDialogProgress = new DialogProgressBar(getClient(), "Receiving Replay");
		fDialogProgress.showDialog(this);
	}

}
