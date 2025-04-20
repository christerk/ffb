package com.fumbbl.ffb.server;

import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

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
  private static final String GAME_LOG_PREFIX = "game_";
  public static final String GAME_LOG_SUFFIX = ".log";
  public static final String GZ_SUFFIX = ".gz";
  private final FantasyFootballServer fServer;
  private final File fLogFile, baseLogPath, defaultLogFile;
  private int fLogLevel;
  private final Set<Long> forceLog = new HashSet<>();
  private final Map<Long, File> logFiles = new HashMap<>();

  private final boolean splitLogs;

  public DebugLog(FantasyFootballServer server, File logFile, File baseLogPath, int logLevel) {
    fServer = server;
    fLogFile = logFile;
    this.baseLogPath = baseLogPath;
    this.defaultLogFile = createLogFile(baseLogPath, "default.log");

    splitLogs = Boolean.parseBoolean(server.getProperty(IServerProperty.SERVER_LOG_FILE_SPLIT));

    setLogLevel(logLevel);
    cleanLogsFromCrash();
  }

  private File logPath(Long gameId) {
    File logDir = new File(this.baseLogPath.getAbsolutePath() + File.separator + gameId / 1000);
    //noinspection ResultOfMethodCallIgnored
    logDir.mkdirs();
    return logDir;
  }

  private File[] getLogFolders() {
    return this.baseLogPath.listFiles(pathname ->
      pathname.isDirectory() && pathname.getName().matches("\\d+"));
  }

  private File createLogFile(File logPath, String fileName) {
    return new File(logPath.getAbsolutePath() + File.separator + fileName);
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

      if (splitLogs) {

        try (PrintWriter gameLog = new PrintWriter(new FileWriter(gameLogFile(pGameId), true))) {
          writeLogLine(header, tokenizer, gameLog);
        } catch (IOException ioe) {
					//noinspection CallToPrintStackTrace
					ioe.printStackTrace();
        }
      } else {
        try (PrintWriter out = new PrintWriter(new FileWriter(getLogFile(), true))) {
          writeLogLine(header, tokenizer, out);
        } catch (IOException ioe) {
					//noinspection CallToPrintStackTrace
					ioe.printStackTrace();
        }
      }
    }
  }

  private static void writeLogLine(String header, StringTokenizer tokenizer, PrintWriter gameLog) {
    while (tokenizer.hasMoreTokens()) {
      String line = tokenizer.nextToken();
      gameLog.print(header);
      gameLog.println(line);
    }
    gameLog.flush();
  }

  public boolean isLogging(int pLogLevel) {
    return (getLogLevel() >= pLogLevel);
  }

  private File gameLogFile(long id) {
    if (id > 0) {
      return logFiles.computeIfAbsent(id, this::createLogFile);
    }

    return defaultLogFile;
  }

  public synchronized File createLogFile(Long aLong) {
    File logPath = logPath(aLong);
    return createLogFile(logPath, GAME_LOG_PREFIX + aLong + GAME_LOG_SUFFIX);
  }

  public void closeResources(long id) {
    if (splitLogs) {
      zipLog(logFiles.get(id));
      logFiles.remove(id);
    }
  }

  private void cleanLogsFromCrash() {
    File[] logDirs = getLogFolders();
    logWithOutGameId(IServerLogLevel.INFO, "Looking for unzipped log files");
    List<String> files = Arrays.stream(logDirs).map(logPath -> logPath.list((dir, name) -> name.startsWith(GAME_LOG_PREFIX) && name.endsWith(GAME_LOG_SUFFIX)))
      .filter(Objects::nonNull).flatMap(Arrays::stream).collect(Collectors.toList());

    if (files.isEmpty()) {
      logWithOutGameId(IServerLogLevel.INFO, "No files to process");
      return;
    }

    logWithOutGameId(IServerLogLevel.INFO, "Found " + files.size() + " files to process");

    files.stream().map(name -> name.replace(GAME_LOG_SUFFIX, "").replace(GAME_LOG_PREFIX, ""))
      .forEach(id -> {
        try {
          logWithOutGameId(IServerLogLevel.INFO, "Processing file for id '" + id + "'");
          zipLog(Long.parseLong(id));
        } catch (Exception ex) {
          logWithOutGameId(ex);
        }
      });
  }

  private void zipLog(long id) {
    zipLog(createLogFile(id));
  }

  private void zipLog(File unzipped) {
    File zipped = createZippedFile(unzipped);
    try (PrintWriter out = new PrintWriter(new GZIPOutputStream(new FileOutputStream(zipped, true)));
         BufferedReader in = new BufferedReader(new FileReader(unzipped))) {
      logWithOutGameId(IServerLogLevel.INFO, "Processing " + unzipped.getName());
      String line = in.readLine();
      while (line != null) {
        out.println(line);
        line = in.readLine();
      }
      out.flush();
    } catch (Exception ioe) {
      logWithOutGameId(ioe);
      return;
    }

    if (unzipped.delete()) {
      logWithOutGameId(IServerLogLevel.INFO, "Deleted " + unzipped.getName());
    } else {
      logWithOutGameId(IServerLogLevel.WARN, "Failed to delete " + unzipped.getName());
    }

  }

  public File createZippedFile(File unzipped) {
    return new File(unzipped.getAbsolutePath() + GZ_SUFFIX);
  }

  public Map<String, List<File>> getLogFiles(long id) {
    File[] logDirs = getLogFolders();

    Map<String, List<File>> files = new HashMap<>();

    if (ArrayTool.isProvided(logDirs)) {
      for (String suffix : new String[]{GAME_LOG_SUFFIX, GZ_SUFFIX}) {
        files.put(suffix, new ArrayList<>());
        for (File logDir : logDirs) {
          File[] logs = logDir.listFiles(pathname ->
            pathname.isFile()
              && pathname.getName().startsWith(GAME_LOG_PREFIX + id)
              && pathname.getName().endsWith(suffix));

          if (ArrayTool.isProvided(logs)) {
            files.get(suffix).addAll(Arrays.asList(logs));
          }
        }
      }
    }

    return files;
  }
}
