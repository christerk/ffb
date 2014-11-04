package com.balancedbytes.games.ffb.server.request.fumbbl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandJoinApproved;
import com.balancedbytes.games.ffb.server.request.ServerRequest;
import com.balancedbytes.games.ffb.server.request.ServerRequestProcessor;
import com.balancedbytes.games.ffb.server.util.UtilServerHttpClient;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestCheckAuthorization extends ServerRequest {
  
  private static final Pattern _PATTERN_RESPONSE = Pattern.compile("<response>([^<]+)</response>");

  private Session fSession;
  private String fCoach;
  private String fPassword;
  private long fGameId;
  private String fGameName;
  private String fTeamId;
  private ClientMode fMode;
  
  public FumbblRequestCheckAuthorization(Session pSession, String pCoach, String pPassword, long pGameId, String pGameName, String pTeamId, ClientMode pMode) {
    fSession = pSession;
    fCoach = pCoach;
    fPassword = pPassword;
    fGameId = pGameId;
    fGameName = pGameName;
    fTeamId = pTeamId;
    fMode = pMode;
  }
  
  public Session getSession() {
    return fSession;
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  public String getPassword() {
    return fPassword;
  }
  
  public long getGameId() {
    return fGameId;
  }
  
  public String getGameName() {
    return fGameName;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public ClientMode getMode() {
    return fMode;
  }
  
  @Override
  public void process(ServerRequestProcessor pRequestProcessor) {
    boolean passwordOk = false;
    FantasyFootballServer server = pRequestProcessor.getServer();
    try {
    	setRequestUrl(StringTool.bind(
  	    server.getProperty(IServerProperty.FUMBBL_AUTH_RESPONSE),
  	    new String[] {
  	      URLEncoder.encode(getCoach(), UtilFumbblRequest.CHARACTER_ENCODING),
  	      URLEncoder.encode(getPassword(), UtilFumbblRequest.CHARACTER_ENCODING)
  	    }
    	));
    	server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, getRequestUrl());
      String responseXml = UtilServerHttpClient.fetchPage(getRequestUrl());
    	server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_RESPONSE, responseXml);
      if (StringTool.isProvided(responseXml)) {
        BufferedReader xmlReader = new BufferedReader(new StringReader(responseXml));
        String line = null;
        String response = null;
        while ((line = xmlReader.readLine()) != null) {
          Matcher responseMatcher = _PATTERN_RESPONSE.matcher(line);
          if (responseMatcher.find()) {
            response = responseMatcher.group(1);
          }
        }
        xmlReader.close();
        passwordOk = (StringTool.isProvided(response) && response.startsWith("OK"));
      }
    } catch (IOException ioe) {
      throw new FantasyFootballException(ioe);
    }
    if (passwordOk) {
      InternalServerCommandJoinApproved joinApprovedCommand = new InternalServerCommandJoinApproved(getGameId(), getGameName(), getCoach(), getTeamId(), getMode());
      server.getCommunication().handleCommand(new ReceivedCommand(joinApprovedCommand, getSession()));
    } else {
      server.getCommunication().sendStatus(getSession(), ServerStatus.ERROR_WRONG_PASSWORD, null);
    }
  }

}
