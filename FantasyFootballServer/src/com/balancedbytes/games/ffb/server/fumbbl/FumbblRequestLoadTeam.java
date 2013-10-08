package com.balancedbytes.games.ffb.server.fumbbl;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.admin.IAdminGameIdListener;
import com.balancedbytes.games.ffb.server.net.ChannelManager;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandFumbblTeamLoaded;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FumbblRequestLoadTeam extends FumbblRequest {
  
  private String fCoach;
  private String fTeamId;
  private boolean fHomeTeam; 
  private GameState fGameState;
  private SocketChannel fSender;
  private IAdminGameIdListener fAdminGameIdListener;
  
  public FumbblRequestLoadTeam(GameState pGameState, String pCoach, String pTeamId, boolean pHomeTeam, SocketChannel pSender) {
    fGameState = pGameState;
    fCoach = pCoach;
    fTeamId = pTeamId;
    fHomeTeam = pHomeTeam;
    fSender = pSender;
  }

  public GameState getGameState() {
    return fGameState;
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  public String getTeamId() {
    return fTeamId;
  }
  
  public boolean isHomeTeam() {
    return fHomeTeam;
  }
  
  public SocketChannel getSender() {
    return fSender;
  }
  
  public void setAdminGameIdListener(IAdminGameIdListener pAdminGameIdListener) {
	  fAdminGameIdListener = pAdminGameIdListener;
  }
  
  public IAdminGameIdListener getAdminGameIdListener() {
	  return fAdminGameIdListener;
  }
  
  @Override
  public void process(FumbblRequestProcessor pRequestProcessor) {
    FantasyFootballServer server = pRequestProcessor.getServer();
    Team team = null;
    try {
      team = pRequestProcessor.loadTeam(getTeamId());
    } catch (FantasyFootballException pFantasyFootballException) {
      handleInvalidTeam(pRequestProcessor, getTeamId(), pFantasyFootballException);
    }
    if ((team == null) || !StringTool.isProvided(team.getName())) {
      handleInvalidTeam(pRequestProcessor, getTeamId(), null);
    } else {
    	Roster roster = null;
      try {
        roster = pRequestProcessor.loadRoster(getTeamId());
      } catch (FantasyFootballException pFantasyFootballException) {
        handleInvalidRoster(pRequestProcessor, getTeamId(), pFantasyFootballException);
      }
      if ((roster == null) || !StringTool.isProvided(roster.getName())) {
        handleInvalidRoster(pRequestProcessor, getTeamId(), null);
      } else {
        team.updateRoster(roster);
        server.getGameCache().addTeamToGame(getGameState(), team, isHomeTeam());
        InternalServerCommandFumbblTeamLoaded loadedCommand = new InternalServerCommandFumbblTeamLoaded(getGameState().getId(), getCoach(), isHomeTeam());
        loadedCommand.setSender(getSender());
        loadedCommand.setAdminGameIdListener(getAdminGameIdListener());
        server.getCommunication().handleNetCommand(loadedCommand);
      }
    }
  }
  
  // this might be overkill, we'll see how it does in practice
  private void handleInvalidTeam(FumbblRequestProcessor pRequestProcessor, String pTeamId, Throwable pThrowable) {
    FantasyFootballServer server = pRequestProcessor.getServer();
    server.getDebugLog().log(IServerLogLevel.ERROR, StringTool.bind("Error loading Team $1.", pTeamId));
    server.getDebugLog().log(pThrowable);
    server.getCommunication().sendStatus(getGameState(), ServerStatus.FUMBBL_ERROR, StringTool.bind("Unable to load Team with id $1.", pTeamId));
    closeGame(pRequestProcessor, getGameState());
  }
  
  // this might be overkill, we'll see how it does in practice
  private void handleInvalidRoster(FumbblRequestProcessor pRequestProcessor, String pTeamId, Throwable pThrowable) {
    FantasyFootballServer server = pRequestProcessor.getServer();
    server.getDebugLog().log(IServerLogLevel.ERROR, StringTool.bind("Error loading Roster for Team $1.", pTeamId));
    server.getDebugLog().log(pThrowable);
    server.getCommunication().sendStatus(getGameState(), ServerStatus.FUMBBL_ERROR, StringTool.bind("Unable to load Roster with for Team $1.", pTeamId));
    closeGame(pRequestProcessor, getGameState());
  }
  
  private void closeGame(FumbblRequestProcessor pRequestProcessor, GameState pGameState) {
    if (pGameState != null) {
    	FantasyFootballServer server = pGameState.getServer();
      ChannelManager channelManager = server.getChannelManager();
      SocketChannel[] receivers = channelManager.getChannelsForGameId(pGameState.getId());
      for (int i = 0; i < receivers.length; i++) {
        try {
        	server.getNioServer().removeChannel(receivers[i]);
        } catch (IOException pIoe) {
        	// unable to close this socket - continue with the others
        }
      }
    }
  }
  
}
