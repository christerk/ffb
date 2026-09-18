package com.fumbbl.ffb.client.state.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.IProgressListener;
import com.fumbbl.ffb.client.ReplayControl;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.dialog.DialogProgressBar;
import com.fumbbl.ffb.client.dialog.DialogReplayModeChoice;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.ReplayLogicModule;
import com.fumbbl.ffb.client.ui.ChatComponent;
import com.fumbbl.ffb.client.util.UiDispatcher;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.util.StringTool;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ClientStateReplay extends ClientStateAwt<ReplayLogicModule> implements IDialogCloseListener, IProgressListener {

	private enum DialogState {
		NONE,
		REPLAY_PROGRESS,
		INIT_PROGRESS,
		REPLACE_CHOICE
	}

	private volatile DialogProgressBar fDialogProgress;
	private DialogState currentDialog = DialogState.NONE;
	private final UiDispatcher uiDispatcher = new UiDispatcher();

	public ClientStateReplay(FantasyFootballClientAwt pClient) {
		super(pClient, new ReplayLogicModule(pClient));

		logicModule.setCallbacks(new ReplayCallbacksAwt(this));
	}

	public void setUp() {
		super.setUp();
		setClickable(false);
	}

	public void handleCommand(NetCommand pNetCommand) {
		try {
			logicModule.handleCommand(pNetCommand);
		} catch (Exception e) {
			getClient().logWithOutGameId(e);
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
		return Collections.emptyMap();
	}

	@Override
	public void reinitializeLocalState() {
		super.reinitializeLocalState();

		switch (currentDialog) {
			case REPLAY_PROGRESS:
				fDialogProgress.showDialog(this);
				break;

			case INIT_PROGRESS:
				fDialogProgress.showDialog(this);
				break;
			case REPLACE_CHOICE:
				new DialogReplayModeChoice(getClient()).showDialog(this);
			case NONE:
				break;
		}
	}

	@Override
	public void dialogClosed(IDialog dialog) {
		if (dialog instanceof DialogReplayModeChoice) {
			DialogReplayModeChoice replayModeChoice = (DialogReplayModeChoice) dialog;
			logicModule.replayMode(replayModeChoice.isOnline(), replayModeChoice.getReplayName());
			currentDialog = DialogState.NONE;
			dialog.hideDialog();
		}
	}

	public void updateProgress(int pProgress) {
		updateProgress(pProgress, "Initialized Frame %d of %d.");
	}

	private void updateProgress(int pProgress, String pFormat) {
		if (fDialogProgress == null) {
			return;
		}
		String message = String.format(pFormat, pProgress, fDialogProgress.getMaximum());
		fDialogProgress.updateProgress(pProgress, message);
	}

	public void initProgress(int pMinimum, int pMaximum) {
		if (fDialogProgress == null) {
			return;
		}
		fDialogProgress.setMinimum(pMinimum);
		fDialogProgress.setMaximum(pMaximum);
	}

	private void showProgressDialog() {
		showProgressDialog("Receiving Replay", DialogState.REPLAY_PROGRESS);
	}

	private void showProgressDialog(String title, DialogState dialogState) {
		uiDispatcher.runOnUiThreadAndWait(() -> {
			fDialogProgress = new DialogProgressBar(getClient(), title);
			currentDialog = dialogState;
			fDialogProgress.showDialog(this);
		});
	}

	private void hideProgressDialog() {
		DialogProgressBar progressBar = fDialogProgress;
		currentDialog = DialogState.NONE;
		if (progressBar != null) {
			progressBar.hideDialog();
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
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

	public void setControllingCoach(String controllingCoach) {
		if (!StringTool.isProvided(controllingCoach)) {
			return;
		}

		boolean gainedControl = getClient().getParameters().getCoach().equals(controllingCoach);
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getChat().getReplayControl().setActive(gainedControl);

		userInterface.invokeAndWait(() -> {
			userInterface.getGameMenuBar().updateJoinedCoachesMenu();
			userInterface.getChat().getReplayControl().refresh();
		});

		if (logicModule.isOnline()) {

			String prefix;
			if (gainedControl) {
				prefix = "You are";
			} else {
				prefix = "Coach " + controllingCoach + " is";
			}

			userInterface.getChat().append(TextStyle.SPECTATOR, prefix + " in control of this session");
		}
	}

	public void playStatus(boolean playing, boolean forward) {
		ReplayControl replayControl = getClient().getUserInterface().getChat().getReplayControl();
		if (playing) {
			replayControl.showPlay(forward);
		} else {
			replayControl.showPause();
		}
	}

	public void logCoach(String coach, boolean joined, String replayName) {
		String name;
		String action;
		if (coach.equals(getClient().getParameters().getCoach())) {
			if (!joined) {
				return;
			}
			name = "You";
			action = "joined session \"" + replayName + "\" successfully";
		} else {
			name = "Coach " + coach;
			action = (joined ? "joined" : "left");
		}

		ChatComponent chat = getClient().getUserInterface().getChat();
		chat.append(TextStyle.SPECTATOR, name + " " + action);
	}

	public void updateCoaches(List<String> ignored) {
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.invokeAndWait(() -> {
			userInterface.refreshSideBars();
			userInterface.getGameMenuBar().updateJoinedCoachesMenu();
		});
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
			clientStateReplay.hideProgressDialog();
		}

		@Override
		public void startReplayerInit() {
			clientStateReplay.showProgressDialog("Initializing Replay", DialogState.INIT_PROGRESS);
		}

		@Override
		public void replayerInitialized() {
			clientStateReplay.hideProgressDialog();
		}

		@Override
		public void promptForReplayChoice() {
			clientStateReplay.uiDispatcher.runOnUiThreadAndWait(() -> {
				clientStateReplay.currentDialog = DialogState.REPLACE_CHOICE;
				new DialogReplayModeChoice(clientStateReplay.getClient()).showDialog(clientStateReplay);
			});
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
		public void controlChanged(String controllingCoach) {
			clientStateReplay.setControllingCoach(controllingCoach);
		}

		@Override
		public void playStatus(boolean playing, boolean forward) {
			clientStateReplay.playStatus(playing, forward);
		}

		@Override
		public void coachJoined(String coach, List<String> allCoaches, String replayName) {
			clientStateReplay.logCoach(coach, true, replayName);
			clientStateReplay.updateCoaches(allCoaches);
		}

		@Override
		public void coachLeft(String coach, List<String> allCoaches) {
			clientStateReplay.logCoach(coach, false, null);
			clientStateReplay.updateCoaches(allCoaches);
		}
	}
}
