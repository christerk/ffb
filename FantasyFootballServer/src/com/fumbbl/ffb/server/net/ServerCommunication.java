package com.fumbbl.ffb.server.net;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.GameList;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TeamList;
import com.fumbbl.ffb.json.LZString;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.ZappedPlayer;
import com.fumbbl.ffb.model.change.ModelChangeList;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.net.commands.ClientCommand;
import com.fumbbl.ffb.net.commands.ServerCommand;
import com.fumbbl.ffb.net.commands.ServerCommandAddPlayer;
import com.fumbbl.ffb.net.commands.ServerCommandAdminMessage;
import com.fumbbl.ffb.net.commands.ServerCommandGameList;
import com.fumbbl.ffb.net.commands.ServerCommandGameState;
import com.fumbbl.ffb.net.commands.ServerCommandGameTime;
import com.fumbbl.ffb.net.commands.ServerCommandJoin;
import com.fumbbl.ffb.net.commands.ServerCommandLeave;
import com.fumbbl.ffb.net.commands.ServerCommandModelSync;
import com.fumbbl.ffb.net.commands.ServerCommandPasswordChallenge;
import com.fumbbl.ffb.net.commands.ServerCommandPong;
import com.fumbbl.ffb.net.commands.ServerCommandRemovePlayer;
import com.fumbbl.ffb.net.commands.ServerCommandSound;
import com.fumbbl.ffb.net.commands.ServerCommandStatus;
import com.fumbbl.ffb.net.commands.ServerCommandTalk;
import com.fumbbl.ffb.net.commands.ServerCommandTeamList;
import com.fumbbl.ffb.net.commands.ServerCommandTeamSetupList;
import com.fumbbl.ffb.net.commands.ServerCommandUnzapPlayer;
import com.fumbbl.ffb.net.commands.ServerCommandUserSettings;
import com.fumbbl.ffb.net.commands.ServerCommandVersion;
import com.fumbbl.ffb.net.commands.ServerCommandZapPlayer;
import com.fumbbl.ffb.report.ReportList;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.handler.IReceivedCommandHandler;
import com.fumbbl.ffb.server.net.commands.InternalServerCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandSocketClosed;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommunication implements Runnable, IReceivedCommandHandler {

	private boolean fStopped;
	private final BlockingQueue<ReceivedCommand> fCommandQueue;
	private final FantasyFootballServer fServer;
	private boolean fCommandCompression;

	public ServerCommunication(FantasyFootballServer pServer) {
		fServer = pServer;
		fCommandQueue = new LinkedBlockingQueue<>();
		String commandCompression = (fServer != null) ? fServer.getProperty(IServerProperty.SERVER_COMMAND_COMPRESSION)
				: null;
		if (StringTool.isProvided(commandCompression)) {
			fCommandCompression = Boolean.parseBoolean(commandCompression);
		}
	}

	public boolean handleCommand(ReceivedCommand command) {
		if (fStopped) {
			return false;
		}
		return fCommandQueue.offer(command);
	}

	public boolean handleCommand(InternalServerCommand internalCommand) {
		return handleCommand(new ReceivedCommand(internalCommand, null));
	}

	public void run() {
		try {
			while (!fStopped) {
				ReceivedCommand command = null;
				try {
					command = fCommandQueue.take();
				} catch (InterruptedException pInterruptedException) {
					// continue with receivedCommand == null
				}
				handleCommandInternal(command);
			}
		} catch (Exception pException) {
			getServer().getDebugLog().logWithOutGameId(pException);
			System.exit(99);
		}
	}

	private void handleCommandInternal(ReceivedCommand command) {

		if (command == null) {
			return;
		}

		getServer().getDebugLog().logClientCommand(IServerLogLevel.DEBUG, command);

		// fetch entropy payload
		if (command.isClientCommand()) {
			ClientCommand clientCommand = (ClientCommand) command.getCommand();
			if (clientCommand.hasEntropy()) {
				getServer().getFortuna().addEntropy(clientCommand.getEntropy());
			}
		}

		try {
			getServer().getCommandHandlerFactory().handleCommand(command);
		} catch (Exception any) {
			GameState gameState = null;

			// fetch the game state if available
			try {
				long gameId = getServer().getSessionManager().getGameIdForSession(command.getSession());
				gameState = getServer().getGameCache().getGameStateById(gameId);
			} catch (Exception ignored) {
			}

			getServer().getDebugLog().log((gameState != null) ? gameState.getId() : -1, any);

			// Attempt to shut down the game.
			shutdownGame(gameState);
		}

		if ((command.getId() != NetCommandId.CLIENT_PING) && (command.getId() != NetCommandId.CLIENT_DEBUG_CLIENT_STATE)) {
			long gameId;
			if (command.isInternalCommand()) {
				gameId = ((InternalServerCommand) command.getCommand()).getGameId();
			} else {
				gameId = getServer().getSessionManager().getGameIdForSession(command.getSession());
			}
			GameState gameState = getServer().getGameCache().getGameStateById(gameId);
			if (gameState != null) {
				try {
					if (command.isInternalCommand()
							|| (getServer().getSessionManager().getSessionOfHomeCoach(gameId) == command.getSession())
							|| (getServer().getSessionManager().getSessionOfAwayCoach(gameId) == command.getSession())) {
						getServer().getSessionManager().setLastPing(command.getSession(), System.currentTimeMillis());
						gameState.handleCommand(command);
					}
				} catch (Exception any) {
					getServer().getDebugLog().log(gameState.getId(), any);
					shutdownGame(gameState);
				}
			}
		}

	}

	private void shutdownGame(GameState gameState) {

		// Sanity checking
		if (gameState == null) {
			return;
		}

		// Send out an error message
		try {
			ServerCommandAdminMessage messageCommand = new ServerCommandAdminMessage(
					new String[] { "This match has entered an invalid state and is shutting down." });
			send(getServer().getSessionManager().getSessionsForGameId(gameState.getId()), messageCommand, false);
		} catch (Exception ignored) {
		}

		// Disconnect clients
		try {
			for (Session session : getServer().getSessionManager().getSessionsForGameId(gameState.getId())) {
				getServer().getCommunication().close(session);
			}
		} catch (Exception ignored) {
		}

	}

	public void shutdown() {
		fStopped = true;
		List<ReceivedCommand> commands = new ArrayList<>();
		fCommandQueue.drainTo(commands);
		for (ReceivedCommand command : commands) {
			handleCommandInternal(command);
		}
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

	public void close(Session pSession) {
		if (pSession == null) {
			return;
		}
		pSession.close();
		handleCommand(new ReceivedCommand(new InternalServerCommandSocketClosed(), pSession));
	}

	public void send(Session pSession, NetCommand command, boolean pLog) {
		if (pLog && (pSession != null) && (command != null)) {
			getServer().getDebugLog().logServerCommand(IServerLogLevel.DEBUG, command, pSession);
		}
		send(pSession, command);
	}

	protected void send(Session[] pSessions, NetCommand command, boolean pLog) {
		if (pLog && ArrayTool.isProvided(pSessions) && (command != null)) {
			getServer().getDebugLog().logServerCommand(IServerLogLevel.DEBUG, command, pSessions);
		}
		for (Session pSession : pSessions) {
			send(pSession, command);
		}
	}

	private void send(Session session, NetCommand command) {

		if ((session == null) || (command == null)) {
			return;
		}

		if (!session.isOpen()) {
			close(session);
			return;
		}

		JsonValue jsonValue = command.toJsonValue();
		if (jsonValue == null) {
			return;
		}

		String textMessage = jsonValue.toString();
		if (fCommandCompression) {
			textMessage = LZString.compressToUTF16(textMessage);
		}

		if (!StringTool.isProvided(textMessage)) {
			return;
		}

		try {
			// Future<Void> future = session.getRemote().sendStringByFuture(textMessage);
			session.getRemote()
				.sendBytesByFuture(ByteBuffer.wrap(textMessage.getBytes(StandardCharsets.UTF_8)));
		} catch (WebSocketException webSocketException) {
			// getServer().getDebugLog().log(IServerLogLevel.WARN,
			// webSocketException.getMessage());
			close(session);
		}

	}

	protected void sendAllSessions(GameState gameState, NetCommand command, boolean addToLog) {
		if ((gameState == null) || (command == null)) {
			return;
		}
		if (addToLog) {
			getServer().getDebugLog().logServerCommand(IServerLogLevel.DEBUG, gameState.getId(), command,
					DebugLog.COMMAND_SERVER_ALL_CLIENTS);
		}
		SessionManager sessionManager = getServer().getSessionManager();
		Session[] allSessions = sessionManager.getSessionsForGameId(gameState.getId());
		send(allSessions, command, false);
		gameState.getGameLog().add((ServerCommand) command);
	}

	protected void sendHomeSession(GameState gameState, NetCommand command) {
		if ((gameState == null) || (command == null)) {
			return;
		}
		getServer().getDebugLog().logServerCommand(IServerLogLevel.DEBUG, gameState.getId(), command,
				DebugLog.COMMAND_SERVER_HOME);
		SessionManager sessionManager = getServer().getSessionManager();
		Session homeSession = sessionManager.getSessionOfHomeCoach(gameState.getId());
		send(homeSession, command, false);
	}

	protected void sendHomeAndSpectatorSessions(GameState gameState, NetCommand command) {
		if ((gameState == null) || (command == null)) {
			return;
		}
		getServer().getDebugLog().logServerCommand(IServerLogLevel.DEBUG, gameState.getId(), command,
				DebugLog.COMMAND_SERVER_HOME_SPECTATORS);
		SessionManager sessionManager = getServer().getSessionManager();
		Session[] sessions = sessionManager.getSessionsWithoutAwayCoach(gameState.getId());
		send(sessions, command, false);
		gameState.getGameLog().add((ServerCommand) command);
	}

	protected void sendAwaySession(GameState gameState, NetCommand command) {
		if ((gameState == null) || (command == null)) {
			return;
		}
		getServer().getDebugLog().logServerCommand(IServerLogLevel.DEBUG, gameState.getId(), command,
				DebugLog.COMMAND_SERVER_AWAY);
		SessionManager sessionManager = getServer().getSessionManager();
		Session awaySession = sessionManager.getSessionOfAwayCoach(gameState.getId());
		send(awaySession, command, false);
	}

	protected void sendAwayAndSpectatorSessions(GameState gameState, NetCommand command) {
		if ((gameState == null) || (command == null)) {
			return;
		}
		getServer().getDebugLog().logServerCommand(IServerLogLevel.DEBUG, gameState.getId(), command,
				DebugLog.COMMAND_SERVER_AWAY_SPECTATORS);
		SessionManager sessionManager = getServer().getSessionManager();
		Session[] sessions = sessionManager.getSessionsWithoutHomeCoach(gameState.getId());
		send(sessions, command, false);
		gameState.getGameLog().add((ServerCommand) command);
	}

	protected void sendSpectatorSessions(GameState gameState, NetCommand command) {
		if ((gameState == null) || (command == null)) {
			return;
		}
		getServer().getDebugLog().logServerCommand(IServerLogLevel.DEBUG, gameState.getId(), command,
				DebugLog.COMMAND_SERVER_SPECTATOR);
		SessionManager sessionManager = getServer().getSessionManager();
		Session[] spectatorSessions = sessionManager.getSessionsOfSpectators(gameState.getId());
		send(spectatorSessions, command, false);
		gameState.getGameLog().add((ServerCommand) command);
	}

	// Server Commands

	public void sendUserSettings(Session pSession, String[] pSettingNames, String[] pSettingValues) {
		ServerCommandUserSettings userSettingsCommand = new ServerCommandUserSettings(pSettingNames, pSettingValues);
		send(pSession, userSettingsCommand, true);
		// not logged in Game Log
	}

	public void sendStatus(Session pSession, ServerStatus pStatus, String pMessage) {
		sendStatus(new Session[] { pSession }, pStatus, pMessage);
		// not logged in Game Log
	}

	public void sendStatus(Session[] pSessions, ServerStatus pStatus, String pMessage) {
		ServerCommandStatus statusCommand = new ServerCommandStatus(pStatus, pMessage);
		send(pSessions, statusCommand, true);
	}

	public void sendAdminMessage(String[] pMessages) {
		ServerCommandAdminMessage messageCommand = new ServerCommandAdminMessage(pMessages);
		SessionManager sessionManager = getServer().getSessionManager();
		Session[] allSessions = sessionManager.getAllSessions();
		send(allSessions, messageCommand, false);
	}

	public void sendStatus(GameState gameState, ServerStatus pStatus, String pMessage) {
		ServerCommandStatus statusCommand = new ServerCommandStatus(pStatus, pMessage);
		statusCommand.setCommandNr(gameState.generateCommandNr());
		sendAllSessions(gameState, statusCommand, true);
	}

	public void sendTeamList(Session pSession, TeamList pTeamList) {
		ServerCommandTeamList teamListCommand = new ServerCommandTeamList(pTeamList);
		send(pSession, teamListCommand, true);
		// not logged in Game Log
	}

	public void sendGameList(Session pSession, GameList pGameList) {
		ServerCommandGameList gameListCommand = new ServerCommandGameList(pGameList);
		send(pSession, gameListCommand, true);
		// not logged in Game Log
	}

	public void sendPasswordChallenge(Session pSession, String pChallenge) {
		ServerCommandPasswordChallenge passwordChallengeCommand = new ServerCommandPasswordChallenge(pChallenge);
		send(pSession, passwordChallengeCommand, true);
		// not logged in Game Log
	}

	public void sendVersion(Session pSession, String pServerVersion, String pClientVersion, String[] pClientProperties,
			String[] pClientPropertyValues) {
		ServerCommandVersion versionCommand = new ServerCommandVersion(pServerVersion, pClientVersion, pClientProperties,
				pClientPropertyValues);
		send(pSession, versionCommand, true);
		// not logged in Game Log
	}

	public void sendJoin(Session[] pSessions, String pCoach, ClientMode pMode, String[] pPlayers, int pSpectators) {
		ServerCommandJoin joinCommand = new ServerCommandJoin(pCoach, pMode, pPlayers, pSpectators);
		send(pSessions, joinCommand, true);
		// not logged in Game Log
	}

	public void sendLeave(Session[] pSessions, String pCoach, ClientMode pMode, int pSpectators) {
		ServerCommandLeave leaveCommand = new ServerCommandLeave(pCoach, pMode, pSpectators);
		send(pSessions, leaveCommand, true);
		// not logged in Game Log
	}

	public void sendGameTime(GameState gameState) {
		if (gameState != null) {
			Game game = gameState.getGame();
			ServerCommandGameTime gameTimeCommand = new ServerCommandGameTime(game.getGameTime(), game.getTurnTime());
			sendAllSessions(gameState, gameTimeCommand, false);
			// not logged in Game Log
		}
	}

	public void sendPong(Session pSession, long pClientTime) {
		ServerCommandPong pongCommand = new ServerCommandPong(pClientTime);
		send(pSession, pongCommand, false);
		// not logged in Game Log
	}

	public void sendGameState(Session pSession, GameState gameState) {
		ServerCommandGameState gameStateCommand = new ServerCommandGameState(gameState.getGame());
		send(pSession, gameStateCommand, true);
		// not logged in Game Log
	}

	public void sendTalk(Session pSession, String pCoach, String[] pTalk) {
		ServerCommandTalk talkCommand = new ServerCommandTalk(pCoach, pTalk);
		send(pSession, talkCommand, true);
		// not logged in Game Log
	}

	public void sendPlayerTalk(GameState gameState, String pCoach, String pTalk) {
		ServerCommandTalk talkCommand = new ServerCommandTalk(pCoach, pTalk, false);
		sendAllSessions(gameState, talkCommand, true);
		// not logged in Game Log
	}

	public void sendSpectatorTalk(GameState gameState, String pCoach, String pTalk, boolean adminMode) {
		ServerCommandTalk talkCommand = new ServerCommandTalk(pCoach, pTalk, adminMode);
		
		if (adminMode) {
			sendAllSessions(gameState, talkCommand, true);
		} else {
			sendSpectatorSessions(gameState, talkCommand); // not logged in Game Log
		}
	}

	public void sendTeamSetupList(Session pSession, String[] pSetupNames) {
		ServerCommandTeamSetupList teamSetupListCommand = new ServerCommandTeamSetupList(pSetupNames);
		send(pSession, teamSetupListCommand, true);
		// not logged in Game Log
	}

	public void sendGameState(GameState gameState) {
		ServerCommandGameState gameStateCommand = new ServerCommandGameState(gameState.getGame());
		sendHomeAndSpectatorSessions(gameState, gameStateCommand);
		sendAwaySession(gameState, gameStateCommand.transform());
	}

	public void sendAddPlayer(GameState gameState, String pTeamId, RosterPlayer pPlayer, PlayerState pPlayerState,
			PlayerResult pPlayerResult) {
		ServerCommandAddPlayer addPlayersCommand = new ServerCommandAddPlayer(pTeamId, pPlayer, pPlayerState,
				pPlayerResult);
		addPlayersCommand.setCommandNr(gameState.generateCommandNr());
		sendAllSessions(gameState, addPlayersCommand, true);
	}

	public void sendRemovePlayer(GameState gameState, String pPlayerId) {
		ServerCommandRemovePlayer removePlayerCommand = new ServerCommandRemovePlayer(pPlayerId);
		removePlayerCommand.setCommandNr(gameState.generateCommandNr());
		sendAllSessions(gameState, removePlayerCommand, true);
	}

	public void sendSound(GameState gameState, SoundId pSound) {
		ServerCommandSound soundCommand = new ServerCommandSound(pSound);
		soundCommand.setCommandNr(gameState.generateCommandNr());
		sendAllSessions(gameState, soundCommand, true);
	}

	public void sendModelSync(GameState gameState, ModelChangeList pModelChanges, ReportList pReports,
			Animation pAnimation, SoundId pSound, long pGameTime, long pTurnTime) {
		ServerCommandModelSync syncCommand = new ServerCommandModelSync(pModelChanges, pReports, pAnimation, pSound,
				pGameTime, pTurnTime);
		syncCommand.setCommandNr(gameState.generateCommandNr());
		sendHomeAndSpectatorSessions(gameState, syncCommand);
		sendAwaySession(gameState, syncCommand.transform(gameState.getGame().getRules()));
	}

	public void sendZapPlayer(GameState gameState, RosterPlayer player) {
		ServerCommandZapPlayer commandZapPlayer = new ServerCommandZapPlayer(player.getId(), player.getTeam().getId());
		sendAllSessions(gameState, commandZapPlayer, true);
	}

	public void sendUnzapPlayer(GameState gameState, ZappedPlayer player) {
		ServerCommandUnzapPlayer commandUnzapPlayer = new ServerCommandUnzapPlayer(player.getId(),
				player.getTeam().getId());
		sendAllSessions(gameState, commandUnzapPlayer, true);
	}

	public int getQueueLength() {
		return fCommandQueue.size();
	}
}
