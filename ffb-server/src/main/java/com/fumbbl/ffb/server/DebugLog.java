package com.fumbbl.ffb.server;

import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class DebugLog {

	public static final String COMMAND_CLIENT_HOME = " H->";
	public static final String COMMAND_SERVER_HOME = " ->H";
	public static final String COMMAND_SERVER_HOME_SPECTATORS = "->HS";
	public static final String COMMAND_CLIENT_AWAY = " A->";
	public static final String COMMAND_SERVER_AWAY = " ->A";
	public static final String COMMAND_CLIENT_SPECTATOR = " S->";
	public static final String COMMAND_SERVER_SPECTATOR = " ->S";
	public static final String COMMAND_CLIENT_UNKNOWN = " ?->";
	public static final String COMMAND_SERVER_UNKNOWN = " ->?";
	public static final String COMMAND_SERVER_ALL_CLIENTS = "->AC";
	public static final String COMMAND_NO_COMMAND = "----";

	public static final String FUMBBL_REQUEST = " ->F";
	public static final String FUMBBL_RESPONSE = " F->";

	private static final int _GAME_ID_MAX_LENGTH = 8;
	private static final String _ZEROES = "000000000000000000000";
	private static final String _LINES = "---------------------";
	private static final DateFormat _HEADER_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // 2001-07-04T12:08:56.235
	private static final int _TIMESTAMP_LENGTH = 23;
	private static final int _COMMAND_FLAG_LENGTH = 4;

	private final FantasyFootballServer fServer;
	private final File fLogFile;
	private int fLogLevel;
	private final Set<Long> forceLog = new HashSet<>();

	public DebugLog(FantasyFootballServer server, File logFile, int logLevel) {
		fServer = server;
		fLogFile = logFile;
		setLogLevel(logLevel);
	}

	public int getLogLevel() {
		return fLogLevel;
	}

	public void setLogLevel(int logLevel) {
		fLogLevel = logLevel;
	}

	public File getLogFile() {
		return fLogFile;
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

	public void forceLog(long gameId) {
		forceLog.add(gameId);
	}

	public void logClientCommand(int pLogLevel, ReceivedCommand pReceivedCommand) {
		if (!isLogging(pLogLevel) || (pReceivedCommand == null) || (pReceivedCommand.getId() == null)
			|| (pReceivedCommand.getId() == NetCommandId.CLIENT_PING)) {
			return;
		}
		GameState gameState;
		String commandFlag = COMMAND_CLIENT_UNKNOWN;
		Session session = pReceivedCommand.getSession();
		SessionManager sessionManager = getServer().getSessionManager();
		long gameId = sessionManager.getGameIdForSession(session);
		if (gameId > 0) {
			gameState = getServer().getGameCache().getGameStateById(gameId);
			if ((gameState != null) && (gameState.getGame().getStarted() != null)) {
				if (session == sessionManager.getSessionOfHomeCoach(gameState.getId())) {
					commandFlag = COMMAND_CLIENT_HOME;
				} else if (session == sessionManager.getSessionOfAwayCoach(gameState.getId())) {
					commandFlag = COMMAND_CLIENT_AWAY;
				} else {
					commandFlag = COMMAND_CLIENT_SPECTATOR;
				}
			}
		}
		logInternal(gameId, commandFlag, pReceivedCommand.getCommand().toJsonValue().toString());
	}

	public void logServerCommand(int pLogLevel, long pGameId, NetCommand pNetCommand, String pCommandFlag) {
		if (isLogging(pLogLevel) && (pNetCommand != null)) {
			logInternal(pGameId, pCommandFlag, pNetCommand.toJsonValue().toString());
		}
	}

	public void logServerCommand(int pLogLevel, NetCommand pNetCommand, Session[] pSessions) {
		if (isLogging(pLogLevel) && (pNetCommand != null) && (ArrayTool.isProvided(pSessions))) {
			SessionManager sessionManager = getServer().getSessionManager();
			long gameId = sessionManager.getGameIdForSession(pSessions[0]);
			logInternal(gameId, COMMAND_SERVER_ALL_CLIENTS, pNetCommand.toJsonValue().toString());
		}
	}

	public void logServerCommand(int pLogLevel, NetCommand pNetCommand, Session pSession) {
		if (isLogging(pLogLevel) && (pNetCommand != null) && (pSession != null)) {
			String commandFlag = COMMAND_SERVER_UNKNOWN;
			SessionManager sessionManager = getServer().getSessionManager();
			long gameId = sessionManager.getGameIdForSession(pSession);
			if (gameId > 0) {
				GameState gameState = getServer().getGameCache().getGameStateById(gameId);
				if ((gameState != null) && (gameState.getGame().getStarted() != null)) {
					if (sessionManager.getSessionOfHomeCoach(gameState.getId()) == pSession) {
						commandFlag = COMMAND_SERVER_HOME;
					} else if (sessionManager.getSessionOfAwayCoach(gameState.getId()) == pSession) {
						commandFlag = COMMAND_SERVER_AWAY;
					} else {
						if (sessionManager.getCoachForSession(pSession) != null) {
							commandFlag = COMMAND_SERVER_SPECTATOR;
						}
					}
				}
				logInternal(gameId, commandFlag, pNetCommand.toJsonValue().toString());
			}
		}
	}

	public void logWithOutGameId(int pLogLevel, String pLogString) {
		if (isLogging(pLogLevel) && StringTool.isProvided(pLogString)) {
			logInternal(-1, null, pLogString);
		}
	}

	public void logWithOutGameId(int pLogLevel, String pCommandFlag, String pLogString) {
		if (isLogging(pLogLevel) && StringTool.isProvided(pLogString)) {
			logInternal(-1, pCommandFlag, pLogString);
		}
	}

	public void log(int pLogLevel, long pGameId, String pLogString) {
		if ((isLogging(pLogLevel) || forceLog.contains(pGameId)) && StringTool.isProvided(pLogString)) {
			logInternal(pGameId, null, pLogString);
		}
	}

	public void log(int pLogLevel, long pGameId, String pCommandFlag, String pLogString) {
		if ((isLogging(pLogLevel) || forceLog.contains(pGameId)) && StringTool.isProvided(pLogString)) {
			logInternal(pGameId, pCommandFlag, pLogString);
		}
	}

	public void logWithOutGameId(Throwable pThrowable) {
		log(-1, pThrowable);
	}

	public void log(long pGameId, Throwable pThrowable) {
		if (pThrowable != null) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			if (!ArrayTool.isProvided(pThrowable.getStackTrace())) {
				log(IServerLogLevel.ERROR, pGameId, "Filling in stacktrace for " + pThrowable.getClass().getCanonicalName());
				pThrowable = pThrowable.fillInStackTrace();
			}

			if (!ArrayTool.isProvided(pThrowable.getStackTrace())) {
				log(IServerLogLevel.ERROR, pGameId, "No stacktrace for " + pThrowable.getClass().getCanonicalName());
			}

			pThrowable.printStackTrace(printWriter);
			printWriter.flush();
			printWriter.close();
			log(IServerLogLevel.ERROR, pGameId, stringWriter.getBuffer().toString());
		}
	}

	public void logCurrentStep(int pLogLevel, GameState pGameState) {
		if (isLogging(pLogLevel) && (pGameState != null) && (pGameState.getCurrentStep() != null)) {
			String changeStateLog = "Current Step is " + pGameState.getCurrentStep().getId();
			logInternal(pGameState.getId(), null, changeStateLog);
		}
	}

	private void logInternal(long pGameId, String pCommandFlag, String pLogString) {
		StringBuilder headerBuffer = new StringBuilder(_TIMESTAMP_LENGTH + _GAME_ID_MAX_LENGTH + _COMMAND_FLAG_LENGTH);
		headerBuffer.append(_HEADER_TIMESTAMP_FORMAT.format(new Date()));
		headerBuffer.append(" ");
		if (pGameId > 0) {
			String gameStateId = Long.toString(pGameId);
			if (gameStateId.length() <= _GAME_ID_MAX_LENGTH) {
				headerBuffer.append(_ZEROES, 0, _GAME_ID_MAX_LENGTH - gameStateId.length());
				headerBuffer.append(gameStateId);
			} else {
				headerBuffer.append(gameStateId, 0, _GAME_ID_MAX_LENGTH);
			}
		} else {
			headerBuffer.append(_LINES, 0, _GAME_ID_MAX_LENGTH);
		}
		headerBuffer.append(" ");
		if (StringTool.isProvided(pCommandFlag)) {
			headerBuffer.append(pCommandFlag);
		} else {
			headerBuffer.append(COMMAND_NO_COMMAND);
		}
		headerBuffer.append(" ");
		String header = headerBuffer.toString();
		StringTokenizer tokenizer = new StringTokenizer(pLogString, "\r\n");
		// write synchronized to the log, create a new one if necessary
		synchronized (this) {
			try (PrintWriter out = new PrintWriter(new FileWriter(getLogFile(), true))) {
				while (tokenizer.hasMoreTokens()) {
					String line = tokenizer.nextToken();
					out.print(header);
					out.println(line);
				}
				out.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public boolean isLogging(int pLogLevel) {
		return (getLogLevel() >= pLogLevel);
	}

}
