package com.balancedbytes.games.ffb.server.request.fumbbl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.request.ServerRequest;
import com.balancedbytes.games.ffb.server.request.ServerRequestProcessor;
import com.balancedbytes.games.ffb.server.util.UtilHttpClient;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestPasswordChallenge extends ServerRequest {
  
  private static final Pattern _PATTERN_CHALLENGE = Pattern.compile("<challenge>([^<]+)</challenge>");
  
  private String fCoach;
  private Session fSession;
  
  public FumbblRequestPasswordChallenge(String pCoach, Session pSession) {
    fCoach = pCoach;
    fSession = pSession;
  }

  public String getCoach() {
    return fCoach;
  }
  
  public Session getSession() {
    return fSession;
  }
  
  @Override
  public void process(ServerRequestProcessor pRequestProcessor) {
    String challenge = null;
    FantasyFootballServer server = pRequestProcessor.getServer();
    try {
      setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_AUTH_CHALLENGE), URLEncoder.encode(getCoach(), UtilFumbblRequest.CHARACTER_ENCODING)));
    	server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, getRequestUrl());
      String responseXml = UtilHttpClient.fetchPage(getRequestUrl());
      if (StringTool.isProvided(responseXml)) {
	    	server.getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_RESPONSE, responseXml);
	      BufferedReader xmlReader = new BufferedReader(new StringReader(responseXml));
	      String line = null;
	      while ((line = xmlReader.readLine()) != null) {
	        Matcher challengeMatcher = _PATTERN_CHALLENGE.matcher(line);
	        if (challengeMatcher.find()) {
	          challenge = challengeMatcher.group(1);
	          break;
	        }
	      }
	      xmlReader.close();
      }
    } catch (IOException pIoException) {
      throw new FantasyFootballException(pIoException);
    }
    server.getCommunication().sendPasswordChallenge(getSession(), challenge);
  }

}
