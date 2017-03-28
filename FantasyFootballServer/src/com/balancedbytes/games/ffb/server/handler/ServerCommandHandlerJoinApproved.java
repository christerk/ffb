package com.balancedbytes.games.ffb.server.handler;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.TeamList;
import com.balancedbytes.games.ffb.TeamListEntry;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameStartMode;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.ServerCommunication;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandJoinApproved;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestCheckGamestate;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestLoadTeam;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestLoadTeamList;
import com.balancedbytes.games.ffb.server.util.UtilServerStartGame;
import com.balancedbytes.games.ffb.server.util.UtilServerTimer;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerJoinApproved extends ServerCommandHandler {
  
  private static final String _TEST_PREFIX = "test:";
  
  protected ServerCommandHandlerJoinApproved(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.INTERNAL_SERVER_JOIN_APPROVED;
  }

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    
    InternalServerCommandJoinApproved joinApprovedCommand = (InternalServerCommandJoinApproved) pReceivedCommand.getCommand();
    ServerCommunication communication = getServer().getCommunication();
    SessionManager sessionManager = getServer().getSessionManager();
    GameCache gameCache = getServer().getGameCache();
    GameState gameState = null;
    Session session = pReceivedCommand.getSession();

    if (joinApprovedCommand.getGameId() > 0) {
      gameState = loadGameStateById(joinApprovedCommand, session);
            
    } else if (StringTool.isProvided(joinApprovedCommand.getGameName())) {
      gameState = gameCache.getGameStateByName(joinApprovedCommand.getGameName(), true);
      if ((gameState == null) && !getServer().isBlockingNewGames()) {
        boolean testing = (joinApprovedCommand.getGameName().startsWith(_TEST_PREFIX) || getServer().getMode() == ServerMode.STANDALONE);
        gameState = gameCache.createGameState(testing ? GameStartMode.START_TEST_GAME : GameStartMode.START_GAME);
        gameCache.mapGameNameToId(joinApprovedCommand.getGameName(), gameState.getId());
      }      
    }

    if (gameState != null) {

      Game game = gameState.getGame();
    
      if (joinApprovedCommand.getClientMode() == ClientMode.PLAYER) {
        
        if (joinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach()) || joinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamAway().getCoach())) {
          if ((gameState.getStatus() == GameStatus.SCHEDULED) || (game.getStarted() != null)) {
            joinWithoutTeam(gameState, joinApprovedCommand, session);
          } else {
            if (StringTool.isProvided(joinApprovedCommand.getTeamId())) {
              joinWithTeam(gameState, joinApprovedCommand, session);
            } else {
              sendTeamList(gameState, joinApprovedCommand, session);
            }
          }

        } else if (game.getStarted() != null) {
          communication.sendStatus(session, ServerStatus.ERROR_GAME_IN_USE, null);
          
        } else if (!StringTool.isProvided(joinApprovedCommand.getTeamId())) {
          sendTeamList(gameState, joinApprovedCommand, session);

        } else {
          joinWithTeam(gameState, joinApprovedCommand, session);
        }
      
      // ClientMode.SPECTATOR
      } else {
        
        if (checkForLoggedInCoach(gameState, joinApprovedCommand.getCoach(), session)) {
          getServer().getCommunication().sendStatus(session, ServerStatus.ERROR_ALREADY_LOGGED_IN, null);
        } else {
          sessionManager.addSession(session, gameState.getId(), joinApprovedCommand.getCoach(), joinApprovedCommand.getClientMode(), false);
          UtilServerStartGame.sendServerJoin(gameState, session, joinApprovedCommand.getCoach(), false, ClientMode.SPECTATOR);
          if (gameState.getGame().getStarted() != null) {
            UtilServerTimer.syncTime(gameState);
            communication.sendGameState(session, gameState);
          }
        }
        
      }
      
    }
    
  }
  
  private void joinWithoutTeam(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand, Session pSession) {
    Game game = pGameState.getGame();
    if (pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach()) || pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamAway().getCoach())) {
      if (!game.isTesting() && checkForLoggedInCoach(pGameState, pJoinApprovedCommand.getCoach(), pSession)) {
        getServer().getCommunication().sendStatus(pSession, ServerStatus.ERROR_ALREADY_LOGGED_IN, null);
      } else {
        boolean homeTeam = pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach());
        if (UtilServerStartGame.joinGameAsPlayerAndCheckIfReadyToStart(pGameState, pSession, pJoinApprovedCommand.getCoach(), homeTeam)) {
          if (getServer().getMode() == ServerMode.FUMBBL) {
            getServer().getRequestProcessor().add(new FumbblRequestCheckGamestate(pGameState));
          } else {
            UtilServerStartGame.startGame(pGameState);
          }
        }
      }
    }
  }
  
  private void joinWithTeam(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand, Session pSession) {
    if ((pGameState != null) && StringTool.isProvided(pJoinApprovedCommand.getTeamId())) {
      Game game = pGameState.getGame();
      if (!game.isTesting() && checkForLoggedInCoach(pGameState, pJoinApprovedCommand.getCoach(), pSession)) {
        getServer().getCommunication().sendStatus(pSession, ServerStatus.ERROR_ALREADY_LOGGED_IN, null);
      } else {
        boolean homeTeam = (!StringTool.isProvided(game.getTeamHome().getId()) || pJoinApprovedCommand.getTeamId().equals(game.getTeamHome().getId()));
        if (getServer().getMode() == ServerMode.FUMBBL) {
          getServer().getRequestProcessor().add(new FumbblRequestLoadTeam(pGameState, pJoinApprovedCommand.getCoach(), pJoinApprovedCommand.getTeamId(), homeTeam, pSession));
        } else {
          Team team = getServer().getGameCache().getTeamById(pJoinApprovedCommand.getTeamId());
          getServer().getGameCache().addTeamToGame(pGameState, team, homeTeam);
          if (UtilServerStartGame.joinGameAsPlayerAndCheckIfReadyToStart(pGameState, pSession, pJoinApprovedCommand.getCoach(), homeTeam)) {
            UtilServerStartGame.startGame(pGameState);
          }
        }
      }
    }
  }
  
  private boolean checkForLoggedInCoach(GameState pGameState, String pCoach, Session pSession) {
    SessionManager sessionManager = getServer().getSessionManager(); 
    Session[] allSessions = sessionManager.getSessionsForGameId(pGameState.getId());
    for (int i = 0; i < allSessions.length; i++) {
      if (pSession != allSessions[i]) {
        String coach = sessionManager.getCoachForSession(allSessions[i]);
        if (pCoach.equalsIgnoreCase(coach)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private GameState loadGameStateById(InternalServerCommandJoinApproved pJoinApprovedCommand, Session pSession) {
    GameCache gameCache = getServer().getGameCache();
    GameState gameState = gameCache.getGameStateById(pJoinApprovedCommand.getGameId());
    if (gameState != null) {
      return gameState;
    }
    gameState = gameCache.queryFromDb(pJoinApprovedCommand.getGameId());
    if (gameState == null) {
      return null;
    }
    gameCache.addGame(gameState);
    gameCache.queueDbUpdate(gameState, true);  // persist status update
    return gameState;
  }
  
  private void sendTeamList(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand, Session pSession) {
    if (getServer().getMode() == ServerMode.FUMBBL) {
      getServer().getRequestProcessor().add(new FumbblRequestLoadTeamList(pGameState, pJoinApprovedCommand.getCoach(), pSession));
    } else {
      TeamList teamList = new TeamList();
      Team[] teams = getServer().getGameCache().getTeamsForCoach(pJoinApprovedCommand.getCoach());
      for (Team team : teams) {
        TeamListEntry teamEntry = new TeamListEntry();
        teamEntry.init(team);
        teamList.add(teamEntry);
      }
      getServer().getCommunication().sendTeamList(pSession, teamList);
    }
  }
  
}
