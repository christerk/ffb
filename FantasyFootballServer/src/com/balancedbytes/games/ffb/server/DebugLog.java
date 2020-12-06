package com.balancedbytes.games.ffb.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;

public class DebugLog {

	public static final String COMMAND_CLIENT_HOME = " H->";
	public static final String COMMAND_SERVER_HOME = " ->H";
	public static final String COMMAND_SERVER_HOME_SPECTATORS = "->HS";
	public static final String COMMAND_CLIENT_AWAY = " A->";
	public static final String COMMAND_SERVER_AWAY = " ->A";
	public static final String COMMAND_SERVER_AWAY_SPECTATORS = "->AS";
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

	private FantasyFootballServer fServer;
	private File fLogFile;
	private int fLogLevel;

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

	public void logClientCommand(int pLogLevel, ReceivedCommand pReceivedCommand) {
		if (!isLogging(pLogLevel) || (pReceivedCommand == null) || (pReceivedCommand.getId() == null)
				|| (pReceivedCommand.getId() == NetCommandId.CLIENT_PING)) {
			return;
		}
		GameState gameState = null;
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

	public void log(int pLogLevel, String pLogString) {
		if (isLogging(pLogLevel) && StringTool.isProvided(pLogString)) {
			logInternal(-1, null, pLogString);
		}
	}

	public void log(int pLogLevel, String pCommandFlag, String pLogString) {
		if (isLogging(pLogLevel) && StringTool.isProvided(pLogString)) {
			logInternal(-1, pCommandFlag, pLogString);
		}
	}

	public void log(int pLogLevel, long pGameId, String pLogString) {
		if (isLogging(pLogLevel) && StringTool.isProvided(pLogString)) {
			logInternal(pGameId, null, pLogString);
		}
	}

	public void log(int pLogLevel, long pGameId, String pCommandFlag, String pLogString) {
		if (isLogging(pLogLevel) && StringTool.isProvided(pLogString)) {
			logInternal(pGameId, pCommandFlag, pLogString);
		}
	}

	public void log(Throwable pThrowable) {
		log(-1, pThrowable);
	}

	public void log(long pGameId, Throwable pThrowable) {
		if (pThrowable != null) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			pThrowable.printStackTrace(printWriter);
			printWriter.close();
			log(IServerLogLevel.ERROR, pGameId, stringWriter.getBuffer().toString());
		}
	}

//  public void logChangeState(int pLogLevel, GameState pGameState, ServerStateId pServerStateId) {
//    if ((getLogLevel() >= pLogLevel) && (pServerStateId != null)) {
//      StringBuilder changeStateLog = new StringBuilder(50);
//      changeStateLog.append("Change Server State to ").append(pServerStateId);
//      log(pGameState, null, changeStateLog.toString());
//    }
//  }

	public void logCurrentStep(int pLogLevel, GameState pGameState) {
		if (isLogging(pLogLevel) && (pGameState != null) && (pGameState.getCurrentStep() != null)) {
			StringBuilder changeStateLog = new StringBuilder();
			changeStateLog.append("Current Step is ").append(pGameState.getCurrentStep().getId());
			logInternal(pGameState.getId(), null, changeStateLog.toString());
		}
	}

	private void logInternal(long pGameId, String pCommandFlag, String pLogString) {
		StringBuilder headerBuffer = new StringBuilder(_TIMESTAMP_LENGTH + _GAME_ID_MAX_LENGTH + _COMMAND_FLAG_LENGTH);
		headerBuffer.append(_HEADER_TIMESTAMP_FORMAT.format(new Date()));
		headerBuffer.append(" ");
		if (pGameId > 0) {
			String gameStateId = Long.toString(pGameId);
			if (gameStateId.length() <= _GAME_ID_MAX_LENGTH) {
				headerBuffer.append(_ZEROES.substring(0, _GAME_ID_MAX_LENGTH - gameStateId.length()));
				headerBuffer.append(gameStateId);
			} else {
				headerBuffer.append(gameStateId.substring(0, _GAME_ID_MAX_LENGTH));
			}
		} else {
			headerBuffer.append(_LINES.substring(0, _GAME_ID_MAX_LENGTH));
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
			PrintWriter out = null;
			try {
				out = new PrintWriter(new FileWriter(getLogFile(), true));
				while (tokenizer.hasMoreTokens()) {
					String line = tokenizer.nextToken();
					out.print(header);
					out.println(line);
				}
				out.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}

	public boolean isLogging(int pLogLevel) {
		return (getLogLevel() >= pLogLevel);
	}

}
