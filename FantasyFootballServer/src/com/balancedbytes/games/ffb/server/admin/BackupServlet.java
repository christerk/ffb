package com.balancedbytes.games.ffb.server.admin;

import java.io.BufferedWriter;
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
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandBackupGame;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;

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
  protected void doGet(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
    
    String command = pRequest.getPathInfo();
    if ((command != null) && (command.length() > 1) && command.startsWith("/")) {
      command = command.substring(1);
    }
    Map<String, String[]> parameters = pRequest.getParameterMap();

    if (LOAD.equals(command)) {
      
      pResponse.setContentType("application/json;charset=UTF-8");
      BufferedWriter out = null;
      
      try {
        
        out = new BufferedWriter(pResponse.getWriter());
        
        long gameId = parseGameId(ArrayTool.firstElement(parameters.get(_PARAMETER_GAME_ID)));
        GameState gameState = UtilBackup.load(getServer(), gameId);

        if (gameState != null) {
          gameState.toJsonValue().writeTo(out);
        }
        
      } finally {
        if (out != null) {
          out.close();
        }
      }
      
    } else { 
      
      pResponse.setContentType("text/xml;charset=UTF-8");
      TransformerHandler handler = UtilXml.createTransformerHandler(pResponse.getWriter(), true);

      try {
        handler.startDocument();
      } catch (SAXException pSaxException) {
        throw new FantasyFootballException(pSaxException);
      }
      
      UtilXml.startElement(handler, _XML_TAG_BACKUP);
      
      boolean isOk = true;
      
      if (CHALLENGE.equals(command)) {
        isOk = executeChallenge(handler);
      }
      
      if (SAVE.equals(command)) {
        isOk = checkResponse(ArrayTool.firstElement(parameters.get(_PARAMETER_RESPONSE))) && executeSave(handler, parameters);
      }
      
      UtilXml.addValueElement(handler, _XML_TAG_STATUS, isOk ? _STATUS_OK : _STATUS_FAIL); 
      
      UtilXml.endElement(handler, _XML_TAG_BACKUP);
      
      try {
        handler.endDocument();
      } catch (SAXException pSaxException) {
        throw new FantasyFootballException(pSaxException);
      }

    }

  }

  private boolean executeChallenge(TransformerHandler pHandler) {
    boolean isOk = true;
    String challenge = new StringBuilder().append(fServer.getProperty(IServerProperty.BACKUP_SALT)).append(System.currentTimeMillis()).toString();
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
  
  private boolean executeSave(TransformerHandler pHandler, Map<String, String[]> pParameters) {
    String gameIdString = ArrayTool.firstElement(pParameters.get(_PARAMETER_GAME_ID));
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_GAME_ID, gameIdString);
    UtilXml.addEmptyElement(pHandler, _XML_TAG_SAVE, attributes);
    long gameId = parseGameId(gameIdString);
    if (gameId > 0) {
      getServer().getCommunication().handleCommand(new InternalServerCommandBackupGame(gameId));
      return true;
    } else {
      UtilXml.addValueElement(pHandler, _XML_TAG_ERROR, "Invalid or missing gameId parameter");
      return false;
    }
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

}
