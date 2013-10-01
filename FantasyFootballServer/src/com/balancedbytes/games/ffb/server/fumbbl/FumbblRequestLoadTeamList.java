package com.balancedbytes.games.ffb.server.fumbbl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.channels.SocketChannel;

import org.xml.sax.InputSource;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.TeamList;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.util.UtilHttpClient;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.XmlHandler;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestLoadTeamList extends FumbblRequest {
  
  private String fCoach;
  private GameState fGameState;
  private SocketChannel fSender;
  
  public FumbblRequestLoadTeamList(GameState pGameState, String pCoach, SocketChannel pSender) {
    fGameState = pGameState;
    fCoach = pCoach;
    fSender = pSender;
  }

  public GameState getGameState() {
    return fGameState;
  }
  
  public String getCoach() {
    return fCoach;
  }
    
  public SocketChannel getSender() {
    return fSender;
  }
  
  @Override
  public void process(FumbblRequestProcessor pRequestProcessor) {
    FantasyFootballServer server = pRequestProcessor.getServer();
    TeamList teamList = null;
    try {
    	setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_TEAMS), URLEncoder.encode(getCoach(), FumbblRequestProcessor.CHARACTER_ENCODING)));
      String teamsXml = UtilHttpClient.fetchPage(getRequestUrl());
      if (StringTool.isProvided(teamsXml)) {
        BufferedReader xmlReader = new BufferedReader(new StringReader(teamsXml));
        InputSource xmlSource = new InputSource(xmlReader);
        teamList = new TeamList();
        XmlHandler.parse(xmlSource, teamList);
        xmlReader.close();
      }
    } catch (IOException ioe) {
      throw new FantasyFootballException(ioe);
    }
    if (teamList != null) {
      server.getCommunication().sendTeamList(getSender(), teamList);
    }
  }
  
}
