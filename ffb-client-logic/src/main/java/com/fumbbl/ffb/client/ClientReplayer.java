package com.fumbbl.ffb.client;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.handler.ClientCommandHandler;
import com.fumbbl.ffb.client.handler.ClientCommandHandlerMode;
import com.fumbbl.ffb.client.ui.LogComponent;
import com.fumbbl.ffb.dialog.DialogCoinChoiceParameter;
import com.fumbbl.ffb.dialog.DialogStartGameParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.marking.FieldMarker;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.model.change.ModelChangeId;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommand;
import com.fumbbl.ffb.net.commands.ServerCommandModelSync;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public class ClientReplayer implements ActionListener {

	private final FantasyFootballClient fClient;

	private static final int[] _TIMER_SETTINGS = {800, 400, 200, 100, 50, 25, 10};

	private int fFirstCommandNr;

	private final List<ServerCommand> fReplayList;
	private final List<ServerCommand> fUnseenList;
	private final List<Integer> markingAffectingCommands;
	private final Map<Integer, Map<String, String>> markings;
	private int fLastReplayPosition;
	private int fReplaySpeed;
	private boolean fReplayDirectionForward;
	private boolean fStopping;
	private int fUnseenPosition;
	private boolean fSkipping;
	private int activeMarkingCommand = -1;

	private final Timer fTimer;

	public ClientReplayer(FantasyFootballClient pClient) {
		fClient = pClient;
		fReplayList = new ArrayList<>();
		fUnseenList = new ArrayList<>();
		markingAffectingCommands = new ArrayList<>();
		markings = new HashMap<>();
		fLastReplayPosition = -1;
		fReplaySpeed = 1;
		fTimer = new Timer(1000, this);
		fTimer.setInitialDelay(0);
	}

	public ReplayControl getReplayControl() {
		return getClient().getUserInterface().getChat().getReplayControl();
	}

	public boolean isReplaying() {
		if (getClient().getUserInterface() == null) {
			return false;
		}
		return getClient().getUserInterface().getChat().isReplayShown();
	}

	public boolean isReplayingSingleSpeedForward() {
		return (isReplaying() && isReplayDirectionForward() && (getReplaySpeed() <= 1) && !fSkipping);
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void add(ServerCommand pServerCommand) {
		if (pServerCommand != null) {
			if (isReplaying() || fStopping) {
				if (pServerCommand.isReplayable() || (pServerCommand.getId() == NetCommandId.SERVER_TALK)) {
					synchronized (fUnseenList) {
						fUnseenList.add(pServerCommand);
					}
				}
			} else {
				if (pServerCommand.isReplayable()) {
					synchronized (fReplayList) {
						fReplayList.add(pServerCommand);

						if ((pServerCommand.getCommandNr() > 0)
							&& ((fFirstCommandNr == 0) || (pServerCommand.getCommandNr() < fFirstCommandNr))) {
							fFirstCommandNr = pServerCommand.getCommandNr();
						}
					}
				}
			}
		}
	}

	public void init(ServerCommand[] pServerCommands, Set<Integer> markingAffectingCommands, IProgressListener pProgressListener) {
		markingAffectingCommands.addAll(this.markingAffectingCommands);
		this.markingAffectingCommands.clear();
		this.markingAffectingCommands.addAll(markingAffectingCommands);
		Collections.sort(this.markingAffectingCommands);
		markings.clear();

		List<ServerCommand> oldReplayList = new ArrayList<>(fReplayList);
		fReplayList.clear();
		Collections.addAll(fReplayList, pServerCommands);
		fReplayList.addAll(oldReplayList);
		getClient().getUserInterface().getLog().detachLogDocument();
		if (pProgressListener != null) {
			pProgressListener.initProgress(0, getReplaySize() - 1);
		}
		fLastReplayPosition = 0;
		fReplayDirectionForward = true;
		replayTo(getReplaySize(), ClientCommandHandlerMode.INITIALIZING, pProgressListener);
		getClient().getUserInterface().getLog().attachLogDocument();
	}

	private void setReplaySpeed(int pReplaySpeed) {
		fReplaySpeed = pReplaySpeed;
		fTimer.setDelay(_TIMER_SETTINGS[fReplaySpeed]);
		fTimer.setInitialDelay(_TIMER_SETTINGS[fReplaySpeed]);
	}

	public int getReplaySpeed() {
		return fReplaySpeed;
	}

	public void increaseReplaySpeed() {
		if (fReplaySpeed < _TIMER_SETTINGS.length - 1) {
			setReplaySpeed(fReplaySpeed + 1);
		}
	}

	public void decreaseReplaySpeed() {
		if (fReplaySpeed > 0) {
			setReplaySpeed(fReplaySpeed - 1);
		}
	}

	public void play(boolean pDirectionForward) {
		fReplayDirectionForward = pDirectionForward;
		setReplaySpeed(1);
		if (fLastReplayPosition < 0) {
			if (fReplayDirectionForward) {
				fLastReplayPosition = 0;
			} else {
				fLastReplayPosition = Math.max(getReplaySize(), 0);
			}
		}
		resume();
	}

	public void skip(boolean pDirectionForward) {
		boolean oldReplayDirectionForward = fReplayDirectionForward;
		boolean running = fTimer.isRunning();
		if (running) {
			pause();
		}
		int position;
		if (pDirectionForward) {
			position = getReplaySize() - 1;
			for (int i = fLastReplayPosition + 1; i < getReplaySize(); i++) {
				if (isRegularEndTurnCommand(getReplayCommand(i))) {
					position = i;
					break;
				}
			}
		} else {
			position = 0;
			for (int i = fLastReplayPosition - 1; i > 0; i--) {
				if (isRegularEndTurnCommand(getReplayCommand(i))) {
					position = i;
					break;
				}
			}
		}
		fReplayDirectionForward = pDirectionForward;
		fSkipping = true;
		replayTo(position, ClientCommandHandlerMode.REPLAYING, null);
		fSkipping = false;
		fReplayDirectionForward = oldReplayDirectionForward;
		if (running) {
			resume();
		}
	}

	private boolean isRegularEndTurnCommand(ServerCommand pServerCommand) {
		if ((NetCommandId.SERVER_MODEL_SYNC == pServerCommand.getId())
			&& (((ServerCommandModelSync) pServerCommand).getReportList().hasReport(ReportId.TURN_END))) {
			return getClient().getUserInterface().getLog().hasCommandHighlight(pServerCommand.getCommandNr());
		}
		return false;
	}

	public boolean isRunning() {
		return fTimer.isRunning();
	}

	public void actionPerformed(ActionEvent pE) {
		if (fStopping) {
			if (fUnseenPosition < getUnseenSize()) {
				ServerCommand unseenCommand;
				synchronized (fUnseenList) {
					unseenCommand = fUnseenList.get(fUnseenPosition);
				}
				if (unseenCommand != null) {
					if (unseenCommand.getId() == NetCommandId.SERVER_TALK) {
						ClientCommandHandler commandHandler = getClient().getCommandHandlerFactory()
							.getCommandHandler(NetCommandId.SERVER_TALK);
						commandHandler.handleNetCommand(unseenCommand, ClientCommandHandlerMode.INITIALIZING);
					} else {
						synchronized (fReplayList) {
							fReplayList.add(unseenCommand);
						}
						fLastReplayPosition = getReplaySize() - 1;
						replayTo(fLastReplayPosition + 1, ClientCommandHandlerMode.INITIALIZING, null);
					}
				}
				fUnseenPosition++;
			} else {
				fStopping = false;
				fTimer.stop();
				fUnseenList.clear();
				getClient().getUserInterface().getLog().getLogScrollPane().setScrollBarToMaximum();
			}
		} else if (fReplayDirectionForward) {
			if (fLastReplayPosition < getReplaySize()) {
				replayTo(fLastReplayPosition + 1, ClientCommandHandlerMode.REPLAYING, null);
			} else {
				pause();
				getReplayControl().showPause();
			}
		} else {
			if (fLastReplayPosition > 0) {
				replayTo(fLastReplayPosition - 1, ClientCommandHandlerMode.REPLAYING, null);
			} else {
				pause();
				getReplayControl().showPause();
			}
		}
	}

	private int getReplaySize() {
		synchronized (fReplayList) {
			return fReplayList.size();
		}
	}

	private int getUnseenSize() {
		synchronized (fUnseenList) {
			return fUnseenList.size();
		}
	}

	public ServerCommand getReplayCommand(int pPosition) {
		synchronized (fReplayList) {
			return fReplayList.get(pPosition);
		}
	}

	public void pause() {
		if (isRunning()) {
			fTimer.stop();
		}
	}

	public void resume() {
		if (!isRunning()) {
			fTimer.start();
		}
	}

	private void replayTo(int pReplayPosition, ClientCommandHandlerMode pMode, IProgressListener pProgressListener) {
		int start = 0;
		if ((fLastReplayPosition >= 0) && (fLastReplayPosition < pReplayPosition)) {
			start = fLastReplayPosition;
		}
		if (start == 0) {
			Game oldGame = getClient().getGame();
			FieldMarker[] oldFieldMarker = null;
			PlayerMarker[] oldPlayerMarker = null;
			if (oldGame != null) {
				oldFieldMarker = oldGame.getFieldModel().getTransientFieldMarkers();
				oldPlayerMarker = oldGame.getFieldModel().getTransientPlayerMarkers();
			}
			Game game = createGame();
			getClient().setGame(game);

			if (ArrayTool.isProvided(oldFieldMarker)) {
				Arrays.stream(oldFieldMarker).forEach(marker -> game.getFieldModel().addTransient(marker));
			}

			if (ArrayTool.isProvided(oldPlayerMarker)) {
				Arrays.stream(oldPlayerMarker).forEach(marker -> {
					game.getFieldModel().addTransient(marker);
					getClient().getUserInterface().getFieldComponent().getLayerPlayers().updatePlayerMarker(marker);
				});
			}
			getClient().getUserInterface().init(game.getOptions());

		}
		ServerCommand serverCommand = null;
		IFactorySource applicationSource = getClient().getGame().getApplicationSource().forContext(FactoryType.FactoryContext.APPLICATION);
		FactoryManager factoryManager = getClient().getGame().getApplicationSource().getFactoryManager();
		List<Game> gameVersions = new ArrayList<>();
		if (pMode == ClientCommandHandlerMode.INITIALIZING) {
			gameVersions.add(cloneGame(applicationSource, factoryManager));
			getClient().getCommunication().sendLoadPlayerMarkings(0, gameVersions.get(0));

		}
		for (int i = start; i < pReplayPosition; i++) {
			serverCommand = getReplayCommand(i);
			if (serverCommand != null) {
				// System.out.println(serverCommand.toXml(0));
				getClient().getCommandHandlerFactory().handleNetCommand(serverCommand, pMode);
				if (serverCommand.getId() == NetCommandId.SERVER_MODEL_SYNC) {
					ServerCommandModelSync modelSync = (ServerCommandModelSync) serverCommand;
					if (Arrays.stream(modelSync.getModelChanges().getChanges())
						.anyMatch(change -> change.getChangeId() == ModelChangeId.GAME_SET_DIALOG_PARAMETER
							&& change.getValue() instanceof DialogCoinChoiceParameter)) {
						reset(getClient().getGame().getTurnDataAway().getInducementSet());
						reset(getClient().getGame().getTurnDataHome().getInducementSet());
					}
				}
				if (pMode == ClientCommandHandlerMode.INITIALIZING) {
					if (IClientPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO.equals(getClient().getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE))) {
						if (markingAffectingCommands.contains(serverCommand.getCommandNr())) {
							gameVersions.add(cloneGame(applicationSource, factoryManager));
							int index = gameVersions.size() - 1;
							getClient().getCommunication().sendLoadPlayerMarkings(index, gameVersions.get(index));
						}
					}
				} else if (!markings.isEmpty()) {
					applyMarkings(serverCommand.getCommandNr());
				}
			}
			if (pProgressListener != null) {
				pProgressListener.updateProgress((i - start));
			}
		}
		fLastReplayPosition = pReplayPosition;
		if ((serverCommand != null) && (pMode == ClientCommandHandlerMode.REPLAYING)) {
			highlightCommand(serverCommand.getCommandNr());
		}
		refreshUserInterface();
	}

	private void reset(InducementSet inducementSet) {
		inducementSet.getInducementMapping().forEach((type, inducement) -> inducementSet.addInducement(new Inducement(type, inducement.getValue())));
	}

	private Game cloneGame(IFactorySource applicationSource, FactoryManager factoryManager) {
		Game game = new Game(applicationSource, factoryManager);
		game.initFrom(applicationSource, getClient().getGame().toJsonValue());
		return game;
	}

	public void replayToCommand(int pCommandNr) {
		pause();
		getClient().getUserInterface().getChat().getReplayControl().showPause();
		int position = findPositionForCommand(pCommandNr);
		if (position >= 0) {
			fLastReplayPosition = 0;
			fReplayDirectionForward = true;
			fSkipping = true;
			replayTo(position + 1, ClientCommandHandlerMode.REPLAYING, null);
			fSkipping = false;
		}
	}

	private int findPositionForCommand(int pCommandNr) {
		int position = -1;
		for (int i = 0; i < getReplaySize(); i++) {
			ServerCommand serverCommand = getReplayCommand(i);
			if (serverCommand.getCommandNr() == pCommandNr) {
				position = i;
				break;
			}
		}
		return position;
	}

	private void highlightCommand(int pCommandNr) {
		LogComponent log = getClient().getUserInterface().getLog();
		boolean commandShown = log.highlightCommand(pCommandNr, fReplayDirectionForward);
		if (!commandShown) {
			int commandNr = pCommandNr;
			if (fReplayDirectionForward) {
				int lastCommand = getReplayCommand(getReplaySize() - 1).getCommandNr();
				while (!commandShown && (commandNr <= lastCommand)) {
					commandShown = log.highlightCommand(++commandNr, fReplayDirectionForward);
				}

			} else {
				while (!commandShown && (commandNr > log.getMinimumCommandNr())) {
					commandShown = log.highlightCommand(--commandNr, fReplayDirectionForward);
				}
			}
		}
	}

	private Game createGame() {
		Game oldGame = getClient().getGame();
		Game game = new Game(getClient().getFactorySource(), getClient().getFactoryManager());
		game.setId(oldGame.getId());
		game.setTurnTime(oldGame.getTurnTime());
		game.setGameTime(oldGame.getGameTime());
		game.setHomePlaying(true);
		game.setTurnMode(TurnMode.START_GAME);
		game.setDialogParameter(new DialogStartGameParameter());
		game.getFieldModel().setWeather(Weather.NICE);
		game.getOptions().init(oldGame.getOptions());
		GameResult oldGameResult = oldGame.getGameResult();
		addTeam(game, oldGame.getTeamHome(), oldGameResult.getTeamResultHome(), true);
		addTeam(game, oldGame.getTeamAway(), oldGameResult.getTeamResultAway(), false);
		IFactorySource factorySource = oldGame.getRules().forContext(FactoryType.FactoryContext.GAME);
		game.getTurnDataHome().getInducementSet().initFrom(factorySource, new InducementSet().toJsonValue());
		game.getTurnDataAway().getInducementSet().initFrom(factorySource, new InducementSet().toJsonValue());
		game.initializeRules();
		return game;
	}

	private void addTeam(Game pGame, Team pTeam, TeamResult pOldTeamResult, boolean pHomeTeam) {
		Player<?>[] players = pTeam.getPlayers();
		if (pHomeTeam) {
			pGame.getFieldModel().remove(pGame.getTeamHome());
			pGame.setTeamHome(pTeam);
		} else {
			pGame.getFieldModel().remove(pGame.getTeamAway());
			pGame.setTeamAway(pTeam);
		}
		FieldModel fieldModel = pGame.getFieldModel();
		for (Player<?> player : players) {
			// remove mercs, stars and raised players, they will be added via command later
			PlayerType playerType = player.getPlayerType();
			if ((playerType == null) || (playerType == PlayerType.MERCENARY)
				|| (playerType == PlayerType.RAISED_FROM_DEAD)) {
				fieldModel.remove(player);
				pTeam.removePlayer(player);
			} else {
				PlayerResult playerResult = pGame.getGameResult().getPlayerResult(player);
				if (player.getRecoveringInjury() != null) {
					fieldModel.setPlayerState(player, new PlayerState(PlayerState.MISSING));
					playerResult.setSendToBoxReason(SendToBoxReason.MNG);
				} else {
					fieldModel.setPlayerState(player, new PlayerState(PlayerState.RESERVE));
				}
				playerResult.setSeriousInjury(null);
				playerResult.setSeriousInjuryDecay(null);
				player.getEnhancementSources().forEach(player::removeEnhancements);
				playerResult.setCurrentSpps(pOldTeamResult.getPlayerResult(player).getCurrentSpps());
				UtilBox.putPlayerIntoBox(pGame, player);
			}
		}
	}

	private void refreshUserInterface() {
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.refresh();
		getClient().updateClientState();
		userInterface.getDialogManager().updateDialog();
	}

	public boolean isReplayDirectionForward() {
		return fReplayDirectionForward;
	}

	public void start() {
		setReplaySpeed(0);
		getClient().getUserInterface().getChat().showReplay(true);
		getClient().getUserInterface().getLog().enableReplay(true);
		getReplayControl().showPause();
		getReplayControl().setActive(false);
	}

	public void positionOnFirstCommand() {
		LogComponent log = getClient().getUserInterface().getLog();
		replayToCommand(log.findCommandNr(1));
		try {
			SwingUtilities.invokeAndWait(() -> getClient().getUserInterface().getLog().getLogScrollPane().setScrollBarToMinimum());
		} catch (Exception pE) {
			throw new FantasyFootballException(pE);
		}
	}

	public void positionOnLastCommand() {
		fReplayDirectionForward = false;
		fLastReplayPosition = Math.max(getReplaySize() - 1, 0);
		ServerCommand serverCommand = getReplayCommand(fLastReplayPosition);
		if (serverCommand != null) {
			highlightCommand(serverCommand.getCommandNr());
		}
		try {
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeAndWait(() -> getClient().getUserInterface().getLog().getLogScrollPane().setScrollBarToMaximum());
			} else {
				getClient().getUserInterface().getLog().getLogScrollPane().setScrollBarToMaximum();
			}
		} catch (Exception pE) {
			throw new FantasyFootballException(pE);
		}
	}

	public void stop() {
		pause();
		replayTo(getReplaySize(), ClientCommandHandlerMode.REPLAYING, null);
		fUnseenPosition = 0;
		fStopping = true;
		setReplaySpeed(4);
		getClient().getUserInterface().getLog().hideHighlight();
		getClient().getUserInterface().getLog().enableReplay(false);
		getClient().getUserInterface().getChat().showReplay(false);
		fTimer.start();
	}

	public int getFirstCommandNr() {
		return fFirstCommandNr;
	}

	public boolean addMarkingConfigs(int index, Map<String, String> markings) {
		this.markings.put(markingAffectingCommands.get(index), markings);
		if (markingAffectingCommands.size() == this.markings.size()) {
			applyMarkings(fLastReplayPosition);
			return true;
		}
		return false;
	}

	private synchronized void applyMarkings(int commandNr) {
		int relevantCommand = findMarkingAffectingCommand(commandNr);

		if (relevantCommand != activeMarkingCommand) {
			activeMarkingCommand = relevantCommand;
			Map<String, String> currentMarkings = markings.get(activeMarkingCommand);
			if (currentMarkings != null) {
				Game game = getClient().getGame();
				for (Player<?> player : game.getPlayers()) {
					PlayerMarker playerMarker = game.getFieldModel().getTransientPlayerMarker(player.getId());
					if (playerMarker == null) {
						playerMarker = new PlayerMarker(player.getId());
					}
					playerMarker.setHomeText(currentMarkings.get(player.getId()));
					getClient().getGame().getFieldModel().addTransient(playerMarker);
					getClient().getUserInterface().getFieldComponent().getLayerPlayers().updatePlayerMarker(playerMarker);
				}
				getClient().getUserInterface().refresh();
			}
		}
	}

	private int findMarkingAffectingCommand(int commandNr) {
		int relevantCommand = 0;
		for (int mac : this.markingAffectingCommands) {
			if (mac < commandNr) {
				relevantCommand = mac;
			} else {
				break;
			}
		}
		return relevantCommand;
	}

}
