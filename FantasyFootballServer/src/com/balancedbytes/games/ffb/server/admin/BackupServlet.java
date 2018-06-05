package com.balancedbytes.games.ffb.server.admin;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.PasswordChallenge;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.request.ServerRequestSaveReplay;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;

import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class BackupServlet extends HttpServlet {

  public static final String CHALLENGE = "challenge";
  public static final String LOAD = "load";
  public static final String SAVE = "save";

  private static final String _STATUS_OK = "ok";
  private static final String _STATUS_FAIL = "fail";

  private static final String _PARAMETER_RESPONSE = "response";
  private static final String _PARAMETER_GAME_ID = "gameId";

  private static final String _XML_TAG_BACKUP = "backup";
  private static final String _XML_TAG_CHALLENGE = "challenge";
  private static final String _XML_TAG_SAVE = "save";
  private static final String _XML_TAG_ERROR = "error";
  private static final String _XML_TAG_STATUS = "status";

  private static final String _XML_ATTRIBUTE_GAME_ID = "gameId";

  private FantasyFootballServer fServer;
  private String fLastChallenge;

  public BackupServlet(FantasyFootballServer pServer) {
    fServer = pServer;
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }

  @Override
  protected void doGet(HttpServletRequest pRequest, HttpServletResponse pResponse)
      throws ServletException, IOException {

    String command = pRequest.getPathInfo();
    if ((command != null) && (command.length() > 1) && command.startsWith("/")) {
      command = command.substring(1);
    }

    if (CHALLENGE.equals(command)) {
      executeChallenge(pRequest, pResponse);
    }

    if (LOAD.equals(command)) {
      executeLoad(pRequest, pResponse);
    }

    if (SAVE.equals(command)) {
      executeSave(pRequest, pResponse);
    }

  }

  private void executeChallenge(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException {

    pResponse.setContentType("text/xml;charset=UTF-8");

    TransformerHandler handler = UtilXml.createTransformerHandler(pResponse.getWriter(), true);
    try {
      handler.startDocument();
    } catch (SAXException pSaxException) {
      throw new FantasyFootballException(pSaxException);
    }

    UtilXml.startElement(handler, _XML_TAG_BACKUP);

    boolean isOk = true;
    String challenge = new StringBuilder().append(fServer.getProperty(IServerProperty.BACKUP_SALT))
        .append(System.currentTimeMillis()).toString();
    try {
      fLastChallenge = PasswordChallenge.toHexString(PasswordChallenge.md5Encode(challenge.getBytes()));
    } catch (NoSuchAlgorithmException pE) {
      fLastChallenge = null;
    }
    if (fLastChallenge != null) {
      UtilXml.addValueElement(handler, _XML_TAG_CHALLENGE, fLastChallenge);
    } else {
      isOk = false;
    }

    UtilXml.addValueElement(handler, _XML_TAG_STATUS, isOk ? _STATUS_OK : _STATUS_FAIL);

    UtilXml.endElement(handler, _XML_TAG_BACKUP);

    try {
      handler.endDocument();
    } catch (SAXException pSaxException) {
      throw new FantasyFootballException(pSaxException);
    }

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

  private void executeSave(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException {

    pResponse.setContentType("text/xml;charset=UTF-8");
    Map<String, String[]> parameters = pRequest.getParameterMap();
    String gameIdString = ArrayTool.firstElement(parameters.get(_PARAMETER_GAME_ID));
    String response = ArrayTool.firstElement(parameters.get(_PARAMETER_RESPONSE));

    TransformerHandler handler = UtilXml.createTransformerHandler(pResponse.getWriter(), true);
    try {
      handler.startDocument();
    } catch (SAXException pSaxException) {
      throw new FantasyFootballException(pSaxException);
    }

    UtilXml.startElement(handler, _XML_TAG_BACKUP);

    boolean isOk = checkResponse(response);

    if (isOk) {
      AttributesImpl attributes = new AttributesImpl();
      UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_ID, gameIdString);
      UtilXml.addEmptyElement(handler, _XML_TAG_SAVE, attributes);
      long gameId = parseGameId(gameIdString);
      if (gameId > 0) {
        getServer().getRequestProcessor().add(new ServerRequestSaveReplay(gameId));
      } else {
        UtilXml.addValueElement(handler, _XML_TAG_ERROR, "Invalid or missing gameId parameter");
        isOk = false;
      }
    }

    UtilXml.addValueElement(handler, _XML_TAG_STATUS, isOk ? _STATUS_OK : _STATUS_FAIL);

    UtilXml.endElement(handler, _XML_TAG_BACKUP);

    try {
      handler.endDocument();
    } catch (SAXException pSaxException) {
      throw new FantasyFootballException(pSaxException);
    }

  }

  private void executeLoad(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException {

    Map<String, String[]> parameters = pRequest.getParameterMap();
    long gameId = parseGameId(ArrayTool.firstElement(parameters.get(_PARAMETER_GAME_ID)));

    String acceptEncoding = pRequest.getHeader("Accept-Encoding");
    boolean doGzip = false;
    if (StringTool.isProvided(acceptEncoding) && acceptEncoding.contains("gzip")) {
      doGzip = true;
    }
    
    // fServer.getDebugLog().log(IServerLogLevel.WARN, gameId, doGzip ? "Requesting gzipped replay." : "Requesting plain replay.");

    pResponse.setContentType("application/json;charset=UTF-8");

    Closeable out = null;
    try {

      GameState gameState = loadGameState(gameId);
      if (gameState == null) {
        return;
      }

      if (doGzip) {

        pResponse.addHeader("Content-Encoding", "gzip");

        out = new BufferedOutputStream(pResponse.getOutputStream());

        byte[] gzippedJson = UtilJson.gzip(gameState.toJsonValue());

        if (gzippedJson != null) {
          ((BufferedOutputStream) out).write(gzippedJson, 0, gzippedJson.length);
        }

        // getServer().getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay size " + StringTool.formatThousands(gzippedJson.length) + " gzipped.");

      } else {

        out = new BufferedWriter(pResponse.getWriter());
        String jsonString = gameState.toJsonValue().toString();
        ((BufferedWriter) out).write(jsonString);
        
        // getServer().getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay size " + StringTool.formatThousands(jsonString.length()) + " unzipped.");

      }

    } finally {
      if (out != null) {
        out.close();
      }
    }

  }

  private GameState loadGameState(long gameId) {
    GameState gameState = UtilBackup.load(getServer(), gameId);
    if (gameState != null) {
      fServer.getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay loaded from file system."); 
    }
    if (gameState == null) {
      // fallback: try to load gameState from db
      gameState = fServer.getGameCache().queryFromDb(gameId);
      if (gameState != null) {
        fServer.getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay loaded from database."); 
      }
    }
    if ((gameState == null) && (getServer().getMode() == ServerMode.FUMBBL)) {
      // fallback in fumbbl mode
      gameState = loadFromFumbblBackupService(gameId);
      if (gameState != null) {
        fServer.getDebugLog().log(IServerLogLevel.WARN, gameId, "Replay loaded from fumbbl backup service."); 
      }
    }
    return gameState;
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

  private GameState loadFromFumbblBackupService(long gameId) {
    
    // Prepare a JSON request to fetch a replay
    String json = String.format("{ \"cmd\": \"get\", \"replayId\":%d }", gameId);
    Msg request = new Msg(json.getBytes());

    byte[] data = null;
    String msgType = null;
    SocketBase socket = null;
    try {

      // Set up ZeroMQ connection
      Ctx ctx = ZMQ.init(1);
      socket = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
      ZMQ.setSocketOption(socket, ZMQ.ZMQ_RCVTIMEO, 5000);

      socket.connect(fServer.getProperty(IServerProperty.FUMBBL_BACKUP_SERVICE));

      // Send the request over the ZeroMQ socket
      socket.send(request, 0);

      // Receive response
      Msg response = socket.recv(0);
      
      if (response == null) {
    	  throw new Exception("Backup file not available.");
      }
      
      data = response.data();

      if (data.length < 100) {
        msgType = new String(data);
      } else {
        throw new Exception("Protocol violation. Message type frame too large.");
      }

      if (response.hasMore()) {
        // Fetch data frame
        data = socket.recv(0).data();
      } else {
        throw new Exception("Protocol violation. No payload frame available.");
      }

    } catch (Exception e) {
      fServer.getDebugLog().log(gameId, e);
      data = null;
    } finally {
      // Make sure we close the socket to not leak resources.
      if (socket != null) {
        socket.close();
      }
    }

    if (data != null) {
      if ("DATA".equals(msgType)) {
        try {
          GameState gameState = new GameState(fServer);
          gameState.initFrom(UtilJson.gunzip(data));
          return gameState;
        } catch (IOException ioE) {
          fServer.getDebugLog().log(gameId, ioE);
        }
      } else {
        // Deal with the error in some way..
        // the "data" variable should have a JSON encoded error message  structure at this point.
        fServer.getDebugLog().log(IServerLogLevel.ERROR, gameId, new String(data));
      }
    }
    
    return null;
    
  }

}
