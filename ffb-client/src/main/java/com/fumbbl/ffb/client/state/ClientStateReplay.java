package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IProgressListener;
import com.fumbbl.ffb.client.dialog.DialogProgressBar;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.ReplayLogicModule;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.ServerStatus;

import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class ClientStateReplay extends ClientStateAwt<ReplayLogicModule> implements IDialogCloseListener, IProgressListener {

	private DialogProgressBar fDialogProgress;

	protected ClientStateReplay(FantasyFootballClientAwt pClient) {
		super(pClient, new ReplayLogicModule(pClient));

		logicModule.setCallbacks(new ReplayCallbacksAwt(this));
	}

	public void initUI() {
		super.initUI();
		setClickable(false);
	}

	public void handleCommand(NetCommand pNetCommand) {
		logicModule.handleCommand(pNetCommand);
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
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

	private void showProgressDialog() {
		fDialogProgress = new DialogProgressBar(getClient(), "Receiving Replay");
		fDialogProgress.showDialog(this);
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = false;
		if (logicModule.replayStopped(pActionKey)) {
			actionHandled = true;
			getClient().getReplayer().stop();
			getClient().updateClientState();
			getClient().getUserInterface().getGameMenuBar().refresh();
		}
		return actionHandled;
	}

	private static class ReplayCallbacksAwt implements ReplayLogicModule.ReplayCallbacks {

		private final ClientStateReplay clientStateReplay;

		private ReplayCallbacksAwt(ClientStateReplay clientStateReplay) {
			this.clientStateReplay = clientStateReplay;
		}

		@Override
		public void reset() {
			clientStateReplay.showProgressDialog();
		}

		@Override
		public void commandCount(int totalCommands) {
			clientStateReplay.initProgress(0, totalCommands);
		}

		@Override
		public void loadedCommands(int currentSize) {
			clientStateReplay.updateProgress(currentSize, "Received Step %d of %d.");
		}

		@Override
		public void loadDone() {
			clientStateReplay.fDialogProgress.hideDialog();
		}

		@Override
		public void startReplayerInit() {
			clientStateReplay.fDialogProgress = new DialogProgressBar(clientStateReplay.getClient(), "Initializing Replay");
			clientStateReplay.fDialogProgress.showDialog(clientStateReplay);
		}

		@Override
		public void replayerInitialized() {
			clientStateReplay.fDialogProgress.hideDialog();
		}

		@Override
		public IProgressListener progressListener() {
			return this.clientStateReplay;
		}

		@Override
		public void replayUnavailable(ServerStatus status) {
			clientStateReplay.getClient().getUserInterface().getStatusReport().reportStatus(status);
		}
	}
}
