package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IProgressListener;
import com.fumbbl.ffb.client.ReplayControl;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.dialog.DialogProgressBar;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.ReplayLogicModule;
import com.fumbbl.ffb.client.ui.ChatComponent;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.ServerStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public void setUp() {
		super.setUp();
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
		boolean actionHandled;
		if (logicModule.replayStopped(pActionKey)) {
			actionHandled = true;
			getClient().getReplayer().stop();
			getClient().updateClientState();
			getClient().getUserInterface().getGameMenuBar().refresh();
		} else {
			actionHandled = handleResize(pActionKey);
		}
		return actionHandled;
	}

	public void setControl(boolean hasControl) {
		getClient().getUserInterface().getChat().getReplayControl().setActive(hasControl);
	}

	public void playStatus(boolean playing, boolean forward) {
		ReplayControl replayControl = getClient().getUserInterface().getChat().getReplayControl();
		if (playing) {
			replayControl.showPlay(forward);
		} else {
			replayControl.showPause();
		}
	}

	public void logCoach(String coach, boolean joined) {
		String name;
		String action;
		if (coach.equals(getClient().getParameters().getCoach())) {
			if (!joined) {
				return;
			}
			name = "You";
			action = "joined the session successfully";
		} else {
			name = coach;
			action = joined ? "joined" : "left";
		}

		ChatComponent chat = getClient().getUserInterface().getChat();
		chat.append(null, TextStyle.SPECTATOR, name + " " + action);
		chat.append(null, null, null);
	}

	public void updateCoaches(List<String> allCoaches) {
		List<String> filteredCoaches = allCoaches.stream().filter(coach -> !coach.equals(getClient().getParameters().getCoach())).collect(Collectors.toList());
		getClient().getClientData().setSpectatorCount(filteredCoaches.size());
		getClient().getClientData().setSpectators(filteredCoaches);
		getClient().getUserInterface().invokeAndWait(() -> getClient().getUserInterface().refreshSideBars());
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

		@Override
		public void controlChanged(boolean hasControl) {
			clientStateReplay.setControl(hasControl);
		}

		@Override
		public void playStatus(boolean playing, boolean forward) {
			clientStateReplay.playStatus(playing, forward);
		}

		@Override
		public void coachJoined(String coach, List<String> allCoaches) {
			clientStateReplay.logCoach(coach, true);
			clientStateReplay.updateCoaches(allCoaches);
		}

		@Override
		public void coachLeft(String coach, List<String> allCoaches) {
			clientStateReplay.logCoach(coach, false);
			clientStateReplay.updateCoaches(allCoaches);
		}
	}
}
