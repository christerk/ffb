package com.balancedbytes.games.ffb.server.admin;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.GameStatusFactory;
import com.balancedbytes.games.ffb.PasswordChallenge;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.old.DbAdminListByStatusQueryOld;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandCloseGame;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandDeleteGame;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandScheduleGame;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandUploadGame;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class AdminServlet extends HttpServlet {

  public static final String CHALLENGE = "challenge";
  public static final String LIST = "list";
  public static final String SHUTDOWN = "shutdown";
  public static final String CLOSE = "close";
  public static final String CONCEDE = "concede";
  public static final String UPLOAD = "upload";
  public static final String DELETE = "delete";
  public static final String MESSAGE = "message";
  public static final String REFRESH = "refresh";
  public static final String SCHEDULE = "schedule";
  public static final String BLOCK = "block";
  public static final String UNBLOCK = "unblock";

  private static final String _STATUS_OK = "ok";
  private static final String _STATUS_FAIL = "fail";

  private static final String _PARAMETER_RESPONSE = "response";
  private static final String _PARAMETER_GAME_ID = "gameId";
  private static final String _PARAMETER_TEAM_ID = "teamId";
  private static final String _PARAMETER_STATUS = "status";
  private static final String _PARAMETER_MESSSAGE = "message";
  private static final String _PARAMETER_TEAM_HOME_ID = "teamHomeId";
  private static final String _PARAMETER_TEAM_AWAY_ID = "teamAwayId";

  private static final String _XML_TAG_ADMIN = "admin";
  private static final String _XML_TAG_CHALLENGE = "challenge";
  private static final String _XML_TAG_CONCEDE = "concede";
  private static final String _XML_TAG_LIST = "list";
  private static final String _XML_TAG_SHUTDOWN = "shutdown";
  private static final String _XML_TAG_CLOSE = "close";
  private static final String _XML_TAG_UPLOAD = "upload";
  private static final String _XML_TAG_DELETE = "delete";
  private static final String _XML_TAG_MESSAGE = "message";
  private static final String _XML_TAG_ERROR = "error";
  private static final String _XML_TAG_REFRESH = "refresh";
  private static final String _XML_TAG_SCHEDULE = "schedule";
  private static final String _XML_TAG_GAME_ID = "gameId";
  private static final String _XML_TAG_BLOCK = "block";
  private static final String _XML_TAG_UNBLOCK = "unblock";
  private static final String _XML_TAG_STATUS = "status";

  private static final String _XML_ATTRIBUTE_INITIATED = "initiated";
  private static final String _XML_ATTRIBUTE_GAME_ID = "gameId";
  private static final String _XML_ATTRIBUTE_TEAM_ID = "teamId";
  private static final String _XML_ATTRIBUTE_TEAM_HOME_ID = "teamHomeId";
  private static final String _XML_ATTRIBUTE_TEAM_AWAY_ID = "teamAwayId";
  private static final String _XML_ATTRIBUTE_GAME_STATUS = "gameStatus";

  private static final DateFormat _TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // 2001-07-04T12:08:56.235

  private FantasyFootballServer fServer;
  private String fLastChallenge;

  public AdminServlet(FantasyFootballServer pServer) {
    fServer = pServer;
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }

  @Override
  protected void doGet(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {

    boolean isOk = true;
    TransformerHandler handler = UtilXml.createTransformerHandler(pResponse.getWriter(), true);

    try {
      handler.startDocument();
    } catch (SAXException pSaxException) {
      throw new FantasyFootballException(pSaxException);
    }
    
    UtilXml.startElement(handler, _XML_TAG_ADMIN);
    
    String command = pRequest.getPathInfo();
    if ((command != null) && (command.length() > 1) && command.startsWith("/")) {
      command = command.substring(1);
    }
    Map<String, String[]> parameters = pRequest.getParameterMap();

    if (CHALLENGE.equals(command)) {
      isOk = handleChallenge(handler);
    } else {
      isOk = checkResponse(ArrayTool.firstElement(parameters.get(_PARAMETER_RESPONSE)));
      if (isOk) {
        if (SHUTDOWN.equals(command)) {
          isOk = handleShutdown(handler);
        } else if (LIST.equals(command)) {
          isOk = handleList(handler, parameters);
        } else if (BLOCK.equals(command)) {
          isOk = handleBlock(handler, true);
        } else if (UNBLOCK.equals(command)) {
          isOk = handleBlock(handler, false);
        } else if (CLOSE.equals(command)) {
          isOk = handleClose(handler, parameters);
        } else if (CONCEDE.equals(command)) {
          isOk = handleConcede(handler, parameters);
        } else if (UPLOAD.equals(command)) {
          isOk = handleUpload(handler, parameters);
        } else if (DELETE.equals(command)) {
          isOk = handleDelete(handler, parameters);
        } else if (MESSAGE.equals(command)) {
          isOk = handleMessage(handler, parameters);
        } else if (REFRESH.equals(command)) {
          isOk = handleRefresh(handler);
        } else if (SCHEDULE.equals(command)) {
          isOk = handleSchedule(handler, parameters);
        } else {
          isOk = false;
        }
      }
    }

    UtilXml.addValueElement(handler, _XML_TAG_STATUS, isOk ? _STATUS_OK : _STATUS_FAIL); 
    
    UtilXml.endElement(handler, _XML_TAG_ADMIN);
    
    try {
      handler.endDocument();
    } catch (SAXException pSaxException) {
      throw new FantasyFootballException(pSaxException);
    }

  }

  private boolean handleChallenge(TransformerHandler pHandler) {
    boolean isOk = true;
    String challenge = new StringBuilder().append(fServer.getProperty(IServerProperty.ADMIN_SALT)).append(System.currentTimeMillis()).toString();
    try {
      fLastChallenge = PasswordChallenge.toHexString(PasswordChallenge.md5Encode(challenge.getBytes()));
    } catch (NoSuchAlgorithmException pE) {
      fLastChallenge = null;
    }
    if (fLastChallenge != null) {
      UtilXml.addValueElement(pHandler, _XML_TAG_CHALLENGE, fLastChallenge);
    } else {
      isOk = false;
    }
    return isOk;
  }

  private boolean handleSchedule(TransformerHandler pHandler, Map<String, String[]> pParameters) {
    String teamHomeId = ArrayTool.firstElement(pParameters.get(_PARAMETER_TEAM_HOME_ID));
    if (!StringTool.isProvided(teamHomeId) || "0".equals(teamHomeId)) {
      UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "Invalid parameter " + _PARAMETER_TEAM_HOME_ID);
      return false;
    }
    String teamAwayId = ArrayTool.firstElement(pParameters.get(_PARAMETER_TEAM_AWAY_ID));
    if (!StringTool.isProvided(teamAwayId) || "0".equals(teamAwayId)) {
      UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "Invalid parameter " + _PARAMETER_TEAM_AWAY_ID);
      return false;
    }
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_HOME_ID, teamHomeId);
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_AWAY_ID, teamAwayId);
    UtilXml.addEmptyElement(pHandler, _XML_TAG_SCHEDULE, attributes);
    if (getServer().isBlockingNewGames()) {
      UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "No new games allowed");
      return false;
    }
    final AtomicReference<Long> gameIdNotifier = new AtomicReference<Long>();
    InternalServerCommandScheduleGame scheduleCommand = new InternalServerCommandScheduleGame(teamHomeId, teamAwayId);
    scheduleCommand.setAdminGameIdListener(new IAdminGameIdListener() {
      public void setGameId(long pGameId) {
        gameIdNotifier.set(pGameId);
        gameIdNotifier.notify();
      }
    });
    getServer().getCommunication().handleNetCommand(scheduleCommand);
    synchronized (gameIdNotifier) {
      while (gameIdNotifier.get() == null) {
        try {
          gameIdNotifier.wait();
        } catch (InterruptedException pInterruptedException) {
          throw new FantasyFootballException(pInterruptedException);
        }
      }
    }
    UtilXml.addValueElement(pHandler, _XML_TAG_GAME_ID, gameIdNotifier.get());
    return true;
  }

  private boolean handleClose(TransformerHandler pHandler, Map<String, String[]> pParameters) {
    String gameIdString = ArrayTool.firstElement(pParameters.get(_PARAMETER_GAME_ID));
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_ID, gameIdString);
    UtilXml.addEmptyElement(pHandler, _XML_TAG_CLOSE, attributes);
    long gameId = parseGameId(gameIdString);
    if (gameId > 0) {
      getServer().getCommunication().handleNetCommand(new InternalServerCommandCloseGame(gameId));
      return true;
    } else {
      UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "Invalid or missing gameId parameter");
      return false;
    }
  }

  private boolean handleBlock(TransformerHandler pHandler, boolean pBlockingNewGames) {
    if (pBlockingNewGames) {
      getServer().setBlockingNewGames(true);
      UtilXml.addEmptyElement(pHandler, _XML_TAG_BLOCK);
    } else {
      getServer().setBlockingNewGames(false);
      UtilXml.addEmptyElement(pHandler, _XML_TAG_UNBLOCK);
    }
    return true;
  }

  private long parseGameId(String pGameStateId) {
    if (StringTool.isProvided(pGameStateId)) {
      try {
        return Long.parseLong(pGameStateId);
      } catch (NumberFormatException pNfe) {
        // continue and return 0
      }
    }
    return 0;
  }

  private boolean handleDelete(TransformerHandler pHandler, Map<String, String[]> pParameters) {
    String gameIdString = ArrayTool.firstElement(pParameters.get(_PARAMETER_GAME_ID));
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_ID, gameIdString);
    UtilXml.addEmptyElement(pHandler, _XML_TAG_DELETE, attributes);
    long gameId = parseGameId(gameIdString);
    if (gameId > 0) {
      getServer().getCommunication().handleNetCommand(new InternalServerCommandDeleteGame(gameId));
      return true;
    } else {
      UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "Invalid or missing gameId parameter");
      return false;
    }
  }

  private boolean handleMessage(TransformerHandler pHandler, Map<String, String[]> pParameters) {
    String message = ArrayTool.firstElement(pParameters.get(_PARAMETER_MESSSAGE));
    if (StringTool.isProvided(message)) {
      UtilXml.addValueElement(pHandler, _XML_TAG_MESSAGE, StringTool.print(message));
      getServer().getCommunication().sendAdminMessage(new String[] { message });
      return true;
    } else {
      UtilXml.addEmptyElement(pHandler, _XML_TAG_MESSAGE);
      return false;
    }
  }

  private boolean handleUpload(TransformerHandler pHandler, Map<String, String[]> pParameters) {
    String gameIdString = ArrayTool.firstElement(pParameters.get(_PARAMETER_GAME_ID));
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_ID, gameIdString);
    UtilXml.addEmptyElement(pHandler, _XML_TAG_UPLOAD, attributes);
    long gameId = parseGameId(gameIdString);
    if (gameId > 0) {
      getServer().getCommunication().handleNetCommand(new InternalServerCommandUploadGame(gameId));
      return true;
    } else {
      UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "Invalid or missing gameId parameter");
      return false;
    }
  }

  private boolean handleConcede(TransformerHandler pHandler, Map<String, String[]> pParameters) {
    String teamId = ArrayTool.firstElement(pParameters.get(_PARAMETER_TEAM_ID));
    if (!StringTool.isProvided(teamId) || "0".equals(teamId)) {
      UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "Invalid or missing teamId parameter");
      return false;
    }
    String gameIdString = ArrayTool.firstElement(pParameters.get(_PARAMETER_GAME_ID));
    long gameId = parseGameId(gameIdString);
    if (gameId > 0) {
      AttributesImpl attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_ID, gameIdString);
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TEAM_ID, teamId);
      UtilXml.addEmptyElement(pHandler, _XML_TAG_CONCEDE, attributes);
      getServer().getCommunication().handleNetCommand(new InternalServerCommandUploadGame(gameId, teamId));
      return true;
    } else {
      UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "Invalid or missing gameId parameter");
      return false;
    }
  }

  private boolean handleList(TransformerHandler pHandler, Map<String, String[]> pParameters) {
    boolean isOk = true;
    GameStatus status = new GameStatusFactory().forName(ArrayTool.firstElement(pParameters.get(_PARAMETER_STATUS)));
    AttributesImpl attributes = new AttributesImpl();
    if (status != null) {
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_STATUS, status.getName());
      UtilXml.startElement(pHandler, _XML_TAG_LIST, attributes);
      DbAdminListByStatusQueryOld listQuery = (DbAdminListByStatusQueryOld) getServer().getDbQueryFactory().getStatement(
          DbStatementId.ADMIN_LIST_BY_STATUS_QUERY_OLD);
      AdminList adminList = new AdminList();
      listQuery.execute(adminList, status);
      if (adminList.size() > 0) {
        for (AdminListEntry listEntry : adminList.getEntries()) {
          listEntry.addToXml(pHandler);
        }
      }
      UtilXml.endElement(pHandler, _XML_TAG_LIST);
    } else {
      isOk = false;
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_STATUS, pParameters.get(_PARAMETER_STATUS));
      UtilXml.addEmptyElement(pHandler, _XML_TAG_LIST, attributes);
    }
    return isOk;
  }

  private boolean handleShutdown(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INITIATED, _TIMESTAMP_FORMAT.format(new Date()));
    UtilXml.addEmptyElement(pHandler, _XML_TAG_SHUTDOWN, attributes);
    Thread stopThread = new Thread(new Runnable() {
      public void run() {
        getServer().stop(0);
      }
    });
    stopThread.start();
    return true;
  }

  private boolean handleRefresh(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_INITIATED, _TIMESTAMP_FORMAT.format(new Date()));
    UtilXml.addEmptyElement(pHandler, _XML_TAG_REFRESH, attributes);
    getServer().getGameCache().clearRosterCache();
    return true;
  }

  private boolean checkResponse(String pResonse) {
    boolean isOk = (fLastChallenge != null);
    if (isOk) {
      byte[] md5Password = PasswordChallenge.fromHexString(fServer.getProperty(IServerProperty.ADMIN_PASSWORD));
      try {
        String response = PasswordChallenge.createResponse(fLastChallenge, md5Password);
        isOk = response.equals(pResonse);
      } catch (NoSuchAlgorithmException pE) {
        isOk = false;
      } catch (IOException pE) {
        isOk = false;
      }
    }
    fLastChallenge = null;
    return isOk;
  }

}
