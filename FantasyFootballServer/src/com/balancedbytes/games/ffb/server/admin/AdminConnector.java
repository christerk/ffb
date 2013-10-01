package com.balancedbytes.games.ffb.server.admin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.PasswordChallenge;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.query.DbAdminListByStatusQueryOld;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandCloseGame;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandDeleteGame;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandScheduleGame;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandUploadGame;
import com.balancedbytes.games.ffb.server.util.UtilHttpClient;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;

/**
 * 
 * @author Kalimar
 */
public class AdminConnector {

  private static final String _USAGE = "java com.balancedbytes.games.ffb.server.admin.AdminConnector block\n"
  		+ "java com.balancedbytes.games.ffb.server.admin.AdminConnector close <gameId>\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector concede <gameId> <teamId>\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector delete <gameId>\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector list <status>\n"
      + "  [status being one of: scheduled, starting, active, paused, finished or uploaded]\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector message <message>\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector refresh\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector shutdown\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector schedule <teamHomeId> <teamAwayId>\n"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector unblock"
      + "java com.balancedbytes.games.ffb.server.admin.AdminConnector upload <gameId>";

  private static final String _STATUS_OK = "ok";
  private static final String _STATUS_FAIL = "fail";

  private static final String _PARAMETER_OP = "op";
  private static final String _PARAMETER_RESPONSE = "response";
  private static final String _PARAMETER_GAME_ID = "gameId";
  private static final String _PARAMETER_TEAM_ID = "teamId";
  private static final String _PARAMETER_STATUS = "status";
  private static final String _PARAMETER_MESSSAGE = "message";
  private static final String _PARAMETER_TEAM_HOME_ID = "teamHomeId";
  private static final String _PARAMETER_TEAM_AWAY_ID = "teamAwayId";

  private static final String _OP_LIST = "list";
  private static final String _OP_SHUTDOWN = "shutdown";
  private static final String _OP_CLOSE = "close";
  private static final String _OP_CONCEDE = "concede";
  private static final String _OP_UPLOAD = "upload";
  private static final String _OP_DELETE = "delete";
  private static final String _OP_MESSAGE = "message";
  private static final String _OP_REFRESH = "refresh";
  private static final String _OP_SCHEDULE = "schedule";
  private static final String _OP_BLOCK = "block";
  private static final String _OP_UNBLOCK = "unblock";

  private static final String _XML_TAG_ADMIN = "admin";
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

  private static final Pattern _PATTERN_CHALLENGE = Pattern.compile("<challenge>([^<]+)</challenge>");
  private static final DateFormat _TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // 2001-07-04T12:08:56.235

  private FantasyFootballServer fServer;
  private String fLastChallenge;

  public AdminConnector(FantasyFootballServer pServer) {
    fServer = pServer;
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }

  public String handleRequest(Properties pParameters) {

    StringWriter writer = new StringWriter();
    TransformerHandler handler = UtilXml.createTransformerHandler(writer, true);

    try {
      handler.startDocument();
    } catch (SAXException pSaxException) {
      throw new FantasyFootballException(pSaxException);
    }
    
    UtilXml.startElement(handler, _XML_TAG_ADMIN);

    String op = pParameters.getProperty(_PARAMETER_OP);
    boolean isOk = checkResponse(pParameters.getProperty(_PARAMETER_RESPONSE));
    
    if (isOk) {
      if (_OP_SHUTDOWN.equals(op)) {
        isOk = handleShutdown(handler);
      } else if (_OP_LIST.equals(op)) {
        isOk = handleList(handler, pParameters);
      } else if (_OP_BLOCK.equals(op)) {
        isOk = handleBlock(handler, true);
      } else if (_OP_UNBLOCK.equals(op)) {
        isOk = handleBlock(handler, false);
      } else if (_OP_CLOSE.equals(op)) {
        isOk = handleClose(handler, pParameters);
      } else if (_OP_CONCEDE.equals(op)) {
        isOk = handleConcede(handler, pParameters);
      } else if (_OP_UPLOAD.equals(op)) {
        isOk = handleUpload(handler, pParameters);
      } else if (_OP_DELETE.equals(op)) {
        isOk = handleDelete(handler, pParameters);
      } else if (_OP_MESSAGE.equals(op)) {
        isOk = handleMessage(handler, pParameters);
      } else if (_OP_REFRESH.equals(op)) {
        isOk = handleRefresh(handler);
      } else if (_OP_SCHEDULE.equals(op)) {
        isOk = handleSchedule(pParameters, handler);
      } else {
        isOk = false;
      }
    }

    UtilXml.addValueElement(handler, _XML_TAG_STATUS, isOk ? _STATUS_OK : _STATUS_FAIL); 
    
    UtilXml.endElement(handler, _XML_TAG_ADMIN);
    
    try {
      handler.endDocument();
    } catch (SAXException pSaxException) {
      throw new FantasyFootballException(pSaxException);
    }
    
    return writer.toString();

  }
  
  private boolean handleSchedule(Properties pParameters, TransformerHandler pHandler) {
    String teamHomeId = pParameters.getProperty(_PARAMETER_TEAM_HOME_ID);
    if (!StringTool.isProvided(teamHomeId) || "0".equals(teamHomeId)) {
    	UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "Invalid parameter " + _PARAMETER_TEAM_HOME_ID);
    	return false;
    }
    String teamAwayId = pParameters.getProperty(_PARAMETER_TEAM_AWAY_ID);
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
  	scheduleCommand.setAdminGameIdListener(
  		new IAdminGameIdListener() {
				public void setGameId(long pGameId) {
					gameIdNotifier.set(pGameId);
					gameIdNotifier.notify();
				}
			}
  	);
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

  private boolean handleClose(TransformerHandler pHandler, Properties pParameters) {
    String gameIdString = pParameters.getProperty(_PARAMETER_GAME_ID);
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
  
  private boolean handleDelete(TransformerHandler pHandler, Properties pParameters) {
    String gameIdString = pParameters.getProperty(_PARAMETER_GAME_ID);
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

  private boolean handleMessage(TransformerHandler pHandler, Properties pParameters) {
    String message = pParameters.getProperty(_PARAMETER_MESSSAGE);
    if (StringTool.isProvided(message)) {
      UtilXml.addValueElement(pHandler, _XML_TAG_MESSAGE, StringTool.print(message));
      getServer().getCommunication().sendAdminMessage(new String[] { message });
      return true;
    } else {
      UtilXml.addEmptyElement(pHandler, _XML_TAG_MESSAGE);
      return false;
    }
  }

  private boolean handleUpload(TransformerHandler pHandler, Properties pParameters) {
    String gameIdString = pParameters.getProperty(_PARAMETER_GAME_ID);
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
  
  private boolean handleConcede(TransformerHandler pHandler, Properties pParameters) {
    String teamId = pParameters.getProperty(_PARAMETER_TEAM_ID);
    if (!StringTool.isProvided(teamId) || "0".equals(teamId)) {
      UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "Invalid or missing teamId parameter");
      return false;
    }
    String gameIdString = pParameters.getProperty(_PARAMETER_GAME_ID);
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

  private boolean handleList(TransformerHandler pHandler, Properties pParameters) {
    boolean isOk = true;
    GameStatus status = GameStatus.fromName(pParameters.getProperty(_PARAMETER_STATUS));
    AttributesImpl attributes = new AttributesImpl();
    if (status != null) {
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_STATUS, status.getName());
      UtilXml.startElement(pHandler, _XML_TAG_LIST, attributes);
      DbAdminListByStatusQueryOld listQuery = (DbAdminListByStatusQueryOld) getServer().getDbQueryFactory().getStatement(DbStatementId.ADMIN_LIST_BY_STATUS_QUERY_OLD);
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
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_STATUS, pParameters.getProperty(_PARAMETER_STATUS));
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

  public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

    if (!ArrayTool.isProvided(args) || !StringTool.isProvided(args[0])) {

      System.out.println(_USAGE);

    } else {

      BufferedInputStream propertyInputStream = new BufferedInputStream(new FileInputStream("server.ini"));
      Properties properties = new Properties();
      properties.load(propertyInputStream);
      propertyInputStream.close();
    	
      FantasyFootballServer server = new FantasyFootballServer(ServerMode.STANDALONE, properties);
      String adminChallengeUrl = server.getProperty(IServerProperty.ADMIN_CHALLENGE);
      System.out.println(adminChallengeUrl);
      String adminChallengeXml = UtilHttpClient.fetchPage(adminChallengeUrl);
      System.out.println(adminChallengeXml);

      String challenge = null;
      BufferedReader xmlReader = new BufferedReader(new StringReader(adminChallengeXml));
      String line = null;
      while ((line = xmlReader.readLine()) != null) {
        Matcher challengeMatcher = _PATTERN_CHALLENGE.matcher(line);
        if (challengeMatcher.find()) {
          challenge = challengeMatcher.group(1);
          break;
        }
      }
      xmlReader.close();

      byte[] md5Password = PasswordChallenge.fromHexString(server.getProperty(IServerProperty.ADMIN_PASSWORD));
      String response = PasswordChallenge.createResponse(challenge, md5Password);

      if (_OP_SHUTDOWN.equals(args[0])) {
        String shutdownUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_SHUTDOWN), response);
        System.out.println(shutdownUrl);
        String shutdownXml = UtilHttpClient.fetchPage(shutdownUrl);
        System.out.println(shutdownXml);
      }

      if (_OP_REFRESH.equals(args[0])) {
        String refreshUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_REFRESH), response);
        System.out.println(refreshUrl);
        String refreshXml = UtilHttpClient.fetchPage(refreshUrl);
        System.out.println(refreshXml);
      }

      if (_OP_BLOCK.equals(args[0])) {
        String blockUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_BLOCK), response);
        System.out.println(blockUrl);
        String blockXml = UtilHttpClient.fetchPage(blockUrl);
        System.out.println(blockXml);
      }

      if (_OP_UNBLOCK.equals(args[0])) {
        String blockUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_UNBLOCK), response);
        System.out.println(blockUrl);
        String blockXml = UtilHttpClient.fetchPage(blockUrl);
        System.out.println(blockXml);
      }

      if (_OP_LIST.equals(args[0])) {
        String adminListUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_LIST_STATUS), response, args[1]);
        System.out.println(adminListUrl);
        String adminListXml = UtilHttpClient.fetchPage(adminListUrl);
        System.out.println(adminListXml);
      }

      if (_OP_CLOSE.equals(args[0])) {
        String closeUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_CLOSE), response, args[1]);
        System.out.println(closeUrl);
        String closeXml = UtilHttpClient.fetchPage(closeUrl);
        System.out.println(closeXml);
      }

      if (_OP_CONCEDE.equals(args[0])) {
        String concedeUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_CONCEDE), response, args[1], args[2]);
        System.out.println(concedeUrl);
        String concedeXml = UtilHttpClient.fetchPage(concedeUrl);
        System.out.println(concedeXml);
      }

      if (_OP_UPLOAD.equals(args[0])) {
        String uploadUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_UPLOAD), response, args[1]);
        System.out.println(uploadUrl);
        String uploadXml = UtilHttpClient.fetchPage(uploadUrl);
        System.out.println(uploadXml);
      }

      if (_OP_DELETE.equals(args[0])) {
        String deleteUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_DELETE), response, args[1], args[2]);
        System.out.println(deleteUrl);
        String deleteXml = UtilHttpClient.fetchPage(deleteUrl);
        System.out.println(deleteXml);
      }

      if (_OP_MESSAGE.equals(args[0])) {
        String message = URLEncoder.encode(args[1], "UTF-8");
        String messageUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_MESSAGE), response, message);
        System.out.println(messageUrl);
        String messageXml = UtilHttpClient.fetchPage(messageUrl);
        System.out.println(messageXml);
      }

      if (_OP_SCHEDULE.equals(args[0])) {
        String scheduleUrl = StringTool.bind(server.getProperty(IServerProperty.ADMIN_SCHEDULE), response, args[1], args[2]);
        System.out.println(scheduleUrl);
        String scheduleXml = UtilHttpClient.fetchPage(scheduleUrl);
        System.out.println(scheduleXml);
      }

    }

  }

}
