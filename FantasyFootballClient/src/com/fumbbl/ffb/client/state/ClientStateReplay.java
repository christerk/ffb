package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.ClientParameters;
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
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class ClientStateReplay extends ClientState implements IDialogCloseListener, IProgressListener {

	private DialogProgressBar fDialogProgress;
	private List<ServerCommand> fReplayList;
	private boolean replayerInitialized;

	protected ClientStateReplay(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.REPLAY;
	}

	public void enterState() {
		super.enterState();
		setClickable(false);
		ClientParameters parameters = getClient().getParameters();
		ClientReplayer replayer = getClient().getReplayer();
		if (ClientMode.REPLAY == getClient().getMode()) {
			if (StringTool.isProvided(parameters.getAuthentication())) {
				getClient().getCommunication().sendJoin(parameters.getCoach(), parameters.getAuthentication(), 0, null, null, null);
			} else {
				startLoadingReplay(replayer, parameters);
			}
		} else {
			if (fReplayList == null) {
				fReplayList = new ArrayList<>();
				showProgressDialog();
				getClient().getCommunication().sendReplay(0, replayer.getFirstCommandNr(), parameters.getCoach());
			} else {
				replayer.positionOnLastCommand();
				replayer.getReplayControl().setActive(true);
			}
		}
	}

	private void startLoadingReplay(ClientReplayer replayer, ClientParameters parameters) {
		replayer.start();
		getClient().getCommunication().sendReplay(parameters.getGameId(), 0, parameters.getCoach());
	}

	public void handleCommand(NetCommand pNetCommand) {
		ClientReplayer replayer = getClient().getReplayer();
		switch (pNetCommand.getId()) {
			case SERVER_USER_SETTINGS:
				startLoadingReplay(getClient().getReplayer(), getClient().getParameters());
				break;
			case SERVER_REPLAY:
				ServerCommandReplay replayCommand = (ServerCommandReplay) pNetCommand;
				initProgress(0, replayCommand.getTotalNrOfCommands());
				for (ServerCommand command : replayCommand.getReplayCommands()) {
					fReplayList.add(command);
					updateProgress(fReplayList.size(), "Received Step %d of %d.");
				}
				if (!replayerInitialized && (replayCommand.isLastCommand() || fReplayList.size() >= replayCommand.getTotalNrOfCommands())) {
					replayerInitialized = true;
					fDialogProgress.hideDialog();
					// signal server that we've received the full replay and the session can be
					// closed
					if (ClientMode.REPLAY == getClient().getMode()) {
						getClient().getCommunication().sendCloseSession();
					}
					ServerCommand[] replayCommands = fReplayList.toArray(new ServerCommand[0]);
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
				if (ClientMode.REPLAY == getClient().getMode()) {
					if (ServerStatus.REPLAY_UNAVAILABLE == statusCommand.getServerStatus()) {
						getClient().getUserInterface().getStatusReport().reportStatus(statusCommand.getServerStatus());
						getClient().getCommunication().sendCloseSession();
					} else {
						startLoadingReplay(getClient().getReplayer(), getClient().getParameters());
					}
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
