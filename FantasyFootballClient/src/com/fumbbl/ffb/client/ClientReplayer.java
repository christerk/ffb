package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.FieldMarker;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.handler.ClientCommandHandler;
import com.fumbbl.ffb.client.handler.ClientCommandHandlerMode;
import com.fumbbl.ffb.client.ui.LogComponent;
import com.fumbbl.ffb.dialog.DialogStartGameParameter;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamResult;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommand;
import com.fumbbl.ffb.net.commands.ServerCommandModelSync;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilBox;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class ClientReplayer implements ActionListener {

	private final FantasyFootballClient fClient;

	private static final int[] _TIMER_SETTINGS = { 800, 400, 200, 100, 50, 25, 10 };

	private int fFirstCommandNr;

	private List<ServerCommand> fReplayList;
	private final List<ServerCommand> fUnseenList;
	private int fLastReplayPosition;
	private int fReplaySpeed;
	private boolean fReplayDirectionForward;
	private boolean fStopping;
	private int fUnseenPosition;
	private boolean fSkipping;

	private final Timer fTimer;

	public ClientReplayer(FantasyFootballClient pClient) {
		fClient = pClient;
		fReplayList = new ArrayList<>();
		fUnseenList = new ArrayList<>();
		fLastReplayPosition = -1;
		fReplaySpeed = 1;
		fTimer = new Timer(1000, this);
		fTimer.setInitialDelay(0);
	}

	public ReplayControl getReplayControl() {
		return getClient().getUserInterface().getChat().getReplayControl();
	}

	public boolean isReplaying() {
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

	public void init(ServerCommand[] pServerCommands, IProgressListener pProgressListener) {
		List<ServerCommand> oldReplayList = fReplayList;
		fReplayList = new ArrayList<>();
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
		for (int i = start; i < pReplayPosition; i++) {
			serverCommand = getReplayCommand(i);
			if (serverCommand != null) {
				// System.out.println(serverCommand.toXml(0));
				getClient().getCommandHandlerFactory().handleNetCommand(serverCommand, pMode);
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
		if (!commandShown && !fReplayDirectionForward) {
			int commandNr = pCommandNr;
			while (!commandShown && (commandNr > log.getMinimumCommandNr())) {
				commandShown = log.highlightCommand(--commandNr, fReplayDirectionForward);
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
			if ((playerType == null) || (playerType == PlayerType.MERCENARY) || (playerType == PlayerType.STAR)
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
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					getClient().getUserInterface().getLog().getLogScrollPane().setScrollBarToMinimum();
				}
			});
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
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						getClient().getUserInterface().getLog().getLogScrollPane().setScrollBarToMaximum();
					}
				});
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

}
