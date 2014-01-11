package com.balancedbytes.games.ffb.server.fumbbl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.Session;
import org.xml.sax.InputSource;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.PasswordChallenge;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.util.UtilHttpClient;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.XmlHandler;

/**
 * 
 * @author Kalimar
 */
public class FumbblRequestProcessor extends Thread {

  public static final String CHARACTER_ENCODING = "UTF-8";
  private static final Pattern _PATTERN_CHALLENGE = Pattern.compile("<challenge>([^<]+)</challenge>");
  private static final String _UNKNOWN_FUMBBL_ERROR = "Unknown problem accessing Fumbbl.";

  private final FantasyFootballServer fServer;
  private final BlockingQueue<FumbblRequest> fRequestQueue;

  public FumbblRequestProcessor(FantasyFootballServer pServer) {
    fServer = pServer;
    fRequestQueue = new LinkedBlockingQueue<FumbblRequest>();
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }
  
  public boolean add(FumbblRequest pFumbblRequest) {
    return fRequestQueue.offer(pFumbblRequest);
  }

  @Override
  public void run() {
    while (true) { 
      FumbblRequest fumbblRequest = null;
      try {
      	fumbblRequest = fRequestQueue.take();
      } catch (InterruptedException pInterruptedException) {
      	// continue with fumbblRequest == null
      }
    	boolean sent = false;
    	do {
    		try {
	        fumbblRequest.process(this);
	  			sent = true;
	    	} catch (Exception pAnyException) {
	    		getServer().getDebugLog().log(IServerLogLevel.ERROR, StringTool.print(fumbblRequest.getRequestUrl()));
	    		getServer().getDebugLog().log(pAnyException);
	    		try {
	    			Thread.sleep(1000);
	    		} catch (InterruptedException pInterruptedException) {
	    			// just continue
	    		}
	    	}
    	} while (!sent);
    }
  }

  public FumbblGameState processGameStateRequest(String pRequestUrl) {
    FumbblGameState gameState = null;
    try {
      String responseXml = UtilHttpClient.fetchPage(pRequestUrl);
      if (StringTool.isProvided(responseXml)) {
      	getServer().getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_RESPONSE, responseXml);
        BufferedReader xmlReader = new BufferedReader(new StringReader(responseXml));
        InputSource xmlSource = new InputSource(xmlReader);
        gameState = new FumbblGameState(pRequestUrl);
        XmlHandler.parse(xmlSource, gameState);
        xmlReader.close();
      }
    } catch (IOException ioe) {
      throw new FantasyFootballException(ioe);
    }
    return gameState;
  }

  public String getChallengeResponseForFumbblUser() {
    String fumbblUser = getServer().getProperty(IServerProperty.FUMBBL_USER);
    String challenge = getChallengeFor(fumbblUser);
    String fumbblUserPassword = getServer().getProperty(IServerProperty.FUMBBL_PASSWORD);
    return createChallengeResponse(challenge, fumbblUserPassword);
  }  
  
  public String createChallengeResponse(String pChallenge, String pPassword) {
    try {
      byte[] encodedPassword = PasswordChallenge.fromHexString(pPassword);
      return PasswordChallenge.createResponse(pChallenge, encodedPassword);
    } catch (IOException pIoE) {
      throw new FantasyFootballException(pIoE);
    } catch (NoSuchAlgorithmException pNsaE) {
      throw new FantasyFootballException(pNsaE);
    }
  }

  public String getChallengeFor(String pCoach) {
    try {
      String challenge = null;
      String challengeUrl = StringTool.bind(getServer().getProperty(IServerProperty.FUMBBL_AUTH_CHALLENGE), URLEncoder.encode(pCoach, CHARACTER_ENCODING));
    	getServer().getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_REQUEST, challengeUrl);
      String responseXml = UtilHttpClient.fetchPage(challengeUrl);
      if (StringTool.isProvided(responseXml)) {
      	getServer().getDebugLog().log(IServerLogLevel.DEBUG, DebugLog.FUMBBL_RESPONSE, responseXml);
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
      return challenge;
    } catch (IOException pIoE) {
      throw new FantasyFootballException(pIoE);
    }
  }
  
  public void reportFumbblError(GameState pGameState, FumbblGameState pFumbblState) {
    FantasyFootballServer server = pGameState.getServer();
    Session[] sessions = server.getSessionManager().getSessionsForGameId(pGameState.getId());
    if (pFumbblState != null) {
      server.getDebugLog().log(IServerLogLevel.ERROR, pFumbblState.toXml(false));
      server.getCommunication().sendStatus(sessions, ServerStatus.FUMBBL_ERROR, pFumbblState.getDescription());
    } else {
      server.getDebugLog().log(IServerLogLevel.ERROR, _UNKNOWN_FUMBBL_ERROR);
      server.getCommunication().sendStatus(sessions, ServerStatus.FUMBBL_ERROR, _UNKNOWN_FUMBBL_ERROR);
    }
  }
  
  public Team loadTeam(String pTeamId) {
    Team team = null;
    try {
    	String teamUrl = StringTool.bind(getServer().getProperty(IServerProperty.FUMBBL_TEAM), pTeamId);
      String teamXml = UtilHttpClient.fetchPage(teamUrl);
      if (StringTool.isProvided(teamXml)) {
        team = new Team();
        BufferedReader xmlReader = new BufferedReader(new StringReader(teamXml));
        InputSource xmlSource = new InputSource(xmlReader);
        XmlHandler.parse(xmlSource, team);
        xmlReader.close();
      }
    } catch (IOException ioe) {
      throw new FantasyFootballException(ioe);
    }
    return team;
  }
  
  public Roster loadRoster(String pTeamId) {
    Roster roster = null;
    try {
    	String rosterUrl = StringTool.bind(getServer().getProperty(IServerProperty.FUMBBL_ROSTER_TEAM), pTeamId);
      String rosterXml = UtilHttpClient.fetchPage(rosterUrl);
      if (StringTool.isProvided(rosterXml)) {
        roster = new Roster();
        BufferedReader xmlReader = new BufferedReader(new StringReader(rosterXml));
        InputSource xmlSource = new InputSource(xmlReader);
        XmlHandler.parse(xmlSource, roster);
        xmlReader.close();
      }
    } catch (IOException ioe) {
      throw new FantasyFootballException(ioe);
    }
    return roster;
  }
      
}
