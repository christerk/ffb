package com.balancedbytes.games.ffb.server.fumbbl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.channels.SocketChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.util.UtilHttpClient;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestPasswordChallenge extends FumbblRequest {
  
  private static final Pattern _PATTERN_CHALLENGE = Pattern.compile("<challenge>([^<]+)</challenge>");
  
  private String fCoach;
  private SocketChannel fSender;
  
  public FumbblRequestPasswordChallenge(String pCoach, SocketChannel pSender) {
    fCoach = pCoach;
    fSender = pSender;
  }

  public String getCoach() {
    return fCoach;
  }
  
  public SocketChannel getSender() {
    return fSender;
  }
  
  @Override
  public void process(FumbblRequestProcessor pRequestProcessor) {
    String challenge = null;
    FantasyFootballServer server = pRequestProcessor.getServer();
    try {
      setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_AUTH_CHALLENGE), URLEncoder.encode(getCoach(), FumbblRequestProcessor.CHARACTER_ENCODING)));
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
    server.getCommunication().sendPasswordChallenge(getSender(), challenge);
  }

}
