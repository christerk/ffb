package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.ClientParameters;
import com.fumbbl.ffb.client.ClientReplayer;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IProgressListener;
import com.fumbbl.ffb.client.dialog.DialogReplayModeChoice;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.net.commands.ServerCommand;
import com.fumbbl.ffb.net.commands.ServerCommandAutomaticPlayerMarkings;
import com.fumbbl.ffb.net.commands.ServerCommandJoin;
import com.fumbbl.ffb.net.commands.ServerCommandLeave;
import com.fumbbl.ffb.net.commands.ServerCommandReplay;
import com.fumbbl.ffb.net.commands.ServerCommandReplayControl;
import com.fumbbl.ffb.net.commands.ServerCommandReplayStatus;
import com.fumbbl.ffb.net.commands.ServerCommandStatus;
import com.fumbbl.ffb.util.StringTool;

import java.util.*;

/**
 * @author Kalimar
 */
public class ReplayLogicModule extends LogicModule implements IDialogCloseListener {

	private List<ServerCommand> fReplayList;
	private Set<Integer> markingAffectingCommands;
	private boolean replayerInitialized;
	private ReplayCallbacks callbacks;

	public ReplayLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.REPLAY;
	}

	public void setCallbacks(ReplayCallbacks callbacks) {
		this.callbacks = callbacks;
	}

	@Override
	public void setUp() {
		super.setUp();
		markingAffectingCommands = new HashSet<>();
		markingAffectingCommands.add(0);
		ClientParameters parameters = client.getParameters();
		ClientReplayer replayer = client.getReplayer();
		if (ClientMode.REPLAY == client.getMode()) {
			replayer.setControl(false);
			callbacks.controlChanged(false);
			if (StringTool.isProvided(parameters.getAuthentication())) {
				client.getCommunication().sendJoin(parameters.getCoach(), parameters.getAuthentication(), 0, null, null, null);
			} else {
				startLoadingReplay(replayer, parameters);
			}
		} else {
			if (fReplayList == null) {
				fReplayList = new ArrayList<>();
				callbacks.reset();
				client.getCommunication().sendReplay(0, replayer.getFirstCommandNr(), parameters.getCoach());
			} else {
				replayer.positionOnLastCommand();
				replayer.getReplayControl().setActive(true);
			}
		}
	}

	private void startLoadingReplay(ClientReplayer replayer, ClientParameters parameters) {
		replayer.start();
		client.getCommunication().sendReplay(parameters.getGameId(), 0, parameters.getCoach());
	}

	public void handleCommand(NetCommand pNetCommand) {
		boolean loadingDone = false;
		ClientReplayer replayer = client.getReplayer();
		switch (pNetCommand.getId()) {
			case SERVER_USER_SETTINGS:
				startLoadingReplay(client.getReplayer(), client.getParameters());
				break;
			case SERVER_REPLAY:
				ServerCommandReplay replayCommand = (ServerCommandReplay) pNetCommand;
				callbacks.commandCount(replayCommand.getTotalNrOfCommands());
				markingAffectingCommands.addAll(replayCommand.getMarkingAffectingCommands());
				for (ServerCommand command : replayCommand.getReplayCommands()) {
					fReplayList.add(command);
					callbacks.loadedCommands(fReplayList.size());
				}
				if (!replayerInitialized && (replayCommand.isLastCommand() || fReplayList.size() >= replayCommand.getTotalNrOfCommands())) {
					replayerInitialized = true;
					callbacks.loadDone();
					// signal server that we've received the full replay and the session can be
					// closed, only if we are not waiting for auto marking responses
					if (ClientMode.REPLAY == client.getMode()) {
						if (!IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO.equals(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE))) {
							loadingDone = true;
						}
					}
					ServerCommand[] replayCommands = fReplayList.toArray(new ServerCommand[0]);
					callbacks.startReplayerInit();
					replayer.init(replayCommands, this.markingAffectingCommands, callbacks.progressListener());
					callbacks.replayerInitialized();
					if (ClientMode.REPLAY == client.getMode()) {
						replayer.positionOnFirstCommand();
					} else {
						replayer.positionOnLastCommand();
					}
				}
				break;
			case SERVER_GAME_STATE:
				if (ClientMode.REPLAY == client.getMode()) {
					fReplayList = new ArrayList<>();
					callbacks.reset();
				}
				break;
			case SERVER_STATUS:
				ServerCommandStatus statusCommand = (ServerCommandStatus) pNetCommand;
				if (ClientMode.REPLAY == client.getMode()) {
					if (ServerStatus.REPLAY_UNAVAILABLE == statusCommand.getServerStatus()) {
						callbacks.replayUnavailable(statusCommand.getServerStatus());
						client.getCommunication().sendCloseSession();
					} else {
						startLoadingReplay(client.getReplayer(), client.getParameters());
					}
				}
				break;
			case SERVER_AUTOMATIC_PLAYER_MARKINGS:
				ServerCommandAutomaticPlayerMarkings playerMarkings = (ServerCommandAutomaticPlayerMarkings) pNetCommand;
				loadingDone = client.getReplayer().addMarkingConfigs(playerMarkings.getIndex(), playerMarkings.getMarkings());
				break;
			case SERVER_REPLAY_STATUS:
				ServerCommandReplayStatus serverCommandReplayStatus = (ServerCommandReplayStatus) pNetCommand;
				replayer.handleCommand(serverCommandReplayStatus, callbacks);
				break;
			case SERVER_REPLAY_CONTROL:
				ServerCommandReplayControl commandReplayControl = (ServerCommandReplayControl) pNetCommand;
				replayer.setControl(commandReplayControl.isControl());
				callbacks.controlChanged(commandReplayControl.isControl());
				break;
			case SERVER_JOIN:
				ServerCommandJoin commandJoin = (ServerCommandJoin) pNetCommand;
				callbacks.coachJoined(commandJoin.getCoach(), commandJoin.getSpectators());
				break;
			case SERVER_LEAVE:
				ServerCommandLeave commandLeave = (ServerCommandLeave) pNetCommand;
				callbacks.coachLeft(commandLeave.getCoach(), commandLeave.getSpectators());
				break;
			default:
				break;
		}
		if (loadingDone && ClientMode.REPLAY == client.getMode()) {
			new DialogReplayModeChoice(client).showDialog(this);
		}
	}

	private void replayMode(boolean online, String name) {
		String sanitizedName = name.substring(0, Math.min(Constant.REPLAY_NAME_MAX_LENGTH, name.length()));
		if (online && StringTool.isProvided(sanitizedName)) {
			client.getCommunication().sendJoinReplay(sanitizedName, client.getParameters().getCoach());
			client.getReplayer().setOnline(true);
		} else {
			client.getCommunication().sendCloseSession();
			client.getReplayer().setControl(true);
			callbacks.controlChanged(true);
		}
	}

	public boolean replayStopped(ActionKey pActionKey) {
		return (ClientMode.SPECTATOR == client.getMode()) && (pActionKey == ActionKey.MENU_REPLAY);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in replay context");
	}

	@Override
	public void dialogClosed(IDialog dialog) {
		if (dialog instanceof DialogReplayModeChoice) {
			DialogReplayModeChoice replayModeChoice = (DialogReplayModeChoice) dialog;
			replayMode(replayModeChoice.isOnline(), replayModeChoice.getReplayName());
			dialog.hideDialog();
		}
	}

	/**
	 * Implement this interface to react to life cycle events during replay loading.
	 * You must pass an instance of this to the {@link ReplayLogicModule} otherwise loading replays will fail.
	 */
	public interface ReplayCallbacks {
		/**
		 * Called when the list of replay commands is truncated
		 */
		void reset();

		/**
		 * Called when we get the total number of replay commands that will be loaded from the server
		 *
		 * @param totalCommands total number of replay commands
		 */
		void commandCount(int totalCommands);

		/**
		 * Called during command load sequence with the current number of commands loaded so far
		 *
		 * @param currentSize number of commands loaded so far
		 */
		void loadedCommands(int currentSize);

		/**
		 * Marks when all commands have been loaded from the server
		 */
		void loadDone();

		/**
		 * Called before the replayer will be initialized
		 */
		void startReplayerInit();

		/**
		 * Called after replayer is initialized
		 */
		void replayerInitialized();

		/**
		 * The return value will be passed to the replayer to be uses during initialization
		 *
		 * @return progress listener allowing to react on the different stages of replay initialization
		 */
		IProgressListener progressListener();

		/**
		 * Called if the replay was not found on server side, connection will be closed
		 */
		void replayUnavailable(ServerStatus status);

		/**
		 * Called when the control state of the client changes
		 *
		 * @param hasControl true if this client has control of the replay session, if false the front end should disable replay controls
		 */
		void controlChanged(boolean hasControl);

		/**
		 * Called when a replay state command was processed
		 */
		void playStatus(boolean playing, boolean forward);

		/**
		 * Called when a coach joins the session
		 */
		void coachJoined(String coach, List<String> allCoaches);

		/**
		 * Called when a coach leaves the session
		 */
		void coachLeft(String coach, List<String> allCoaches);

	}
}
