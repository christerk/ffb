package com.balancedbytes.games.ffb.server.handler;

import java.nio.channels.SocketChannel;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.TeamList;
import com.balancedbytes.games.ffb.TeamListEntry;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameCacheMode;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestCheckGamestate;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestLoadGame;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestLoadTeam;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestLoadTeamList;
import com.balancedbytes.games.ffb.server.net.ChannelManager;
import com.balancedbytes.games.ffb.server.net.ServerCommunication;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandJoinApproved;
import com.balancedbytes.games.ffb.server.util.UtilStartGame;
import com.balancedbytes.games.ffb.server.util.UtilTimer;
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

  public void handleNetCommand(NetCommand pNetCommand) {
    
    InternalServerCommandJoinApproved joinApprovedCommand = (InternalServerCommandJoinApproved) pNetCommand;
    ServerCommunication communication = getServer().getCommunication();
    ChannelManager channelManager = getServer().getChannelManager();
    GameCache gameCache = getServer().getGameCache();
    GameState gameState = null;

    if (joinApprovedCommand.getGameId() > 0) {
      gameState = loadGameStateById(joinApprovedCommand);
            
    } else if (StringTool.isProvided(joinApprovedCommand.getGameName())) {
      gameState = gameCache.getGameStateByName(joinApprovedCommand.getGameName(), true);
      if ((gameState == null) && !getServer().isBlockingNewGames()) {
        boolean testing = (joinApprovedCommand.getGameName().startsWith(_TEST_PREFIX) || getServer().getMode() == ServerMode.STANDALONE);
        gameState = gameCache.createGameState(testing ? GameCacheMode.START_TEST_GAME : GameCacheMode.START_GAME);
        gameCache.mapGameNameToId(joinApprovedCommand.getGameName(), gameState.getId());
      }
      
    }

    if (gameState != null) {

      Game game = gameState.getGame();
    
      if (joinApprovedCommand.getClientMode() == ClientMode.PLAYER) {
        
        if (joinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach()) || joinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamAway().getCoach())) {
          if ((gameState.getStatus() == GameStatus.SCHEDULED) || (game.getStarted() != null)) {
            joinWithoutTeam(gameState, joinApprovedCommand);
          } else {
            if (StringTool.isProvided(joinApprovedCommand.getTeamId())) {
              joinWithTeam(gameState, joinApprovedCommand);
            } else {
              sendTeamList(gameState, joinApprovedCommand);
            }
          }

        } else if (game.getStarted() != null) {
          communication.sendStatus(joinApprovedCommand.getSender(), ServerStatus.ERROR_GAME_IN_USE, null);
          
        } else if (!StringTool.isProvided(joinApprovedCommand.getTeamId())) {
          sendTeamList(gameState, joinApprovedCommand);

        } else {
          joinWithTeam(gameState, joinApprovedCommand);
        }
      
      // ClientMode.SPECTATOR
      } else {
        
        if (checkForLoggedInCoach(gameState, joinApprovedCommand.getCoach(), joinApprovedCommand.getSender())) {
          getServer().getCommunication().sendStatus(joinApprovedCommand.getSender(), ServerStatus.ERROR_ALREADY_LOGGED_IN, null);
        } else {
          channelManager.addChannel(joinApprovedCommand.getSender(), gameState, joinApprovedCommand.getCoach(), joinApprovedCommand.getClientMode(), false);
          UtilStartGame.sendServerJoin(gameState, joinApprovedCommand.getSender(), joinApprovedCommand.getCoach(), false, ClientMode.SPECTATOR);
          if (gameState.getGame().getStarted() != null) {
            UtilTimer.syncTime(gameState);
            communication.sendGameState(joinApprovedCommand.getSender(), gameState);
          }
        }
        
      }
      
    }
    
  }
  
  private void joinWithoutTeam(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand) {
    Game game = pGameState.getGame();
    if (pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach()) || pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamAway().getCoach())) {
      if (!game.isTesting() && checkForLoggedInCoach(pGameState, pJoinApprovedCommand.getCoach(), pJoinApprovedCommand.getSender())) {
        getServer().getCommunication().sendStatus(pJoinApprovedCommand.getSender(), ServerStatus.ERROR_ALREADY_LOGGED_IN, null);
      } else {
        boolean homeTeam = pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach());
        if (UtilStartGame.joinGameAsPlayerAndCheckIfReadyToStart(pGameState, pJoinApprovedCommand.getSender(), pJoinApprovedCommand.getCoach(), homeTeam)) {
          if (getServer().getMode() == ServerMode.FUMBBL) {
            getServer().getFumbblRequestProcessor().add(new FumbblRequestCheckGamestate(pGameState));
          } else {
            UtilStartGame.startGame(pGameState);
          }
        }
      }
    }
  }
  
  private void joinWithTeam(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand) {
    if ((pGameState != null) && StringTool.isProvided(pJoinApprovedCommand.getTeamId())) {
      Game game = pGameState.getGame();
      if (!game.isTesting() && checkForLoggedInCoach(pGameState, pJoinApprovedCommand.getCoach(), pJoinApprovedCommand.getSender())) {
        getServer().getCommunication().sendStatus(pJoinApprovedCommand.getSender(), ServerStatus.ERROR_ALREADY_LOGGED_IN, null);
      } else {
        boolean homeTeam = (!StringTool.isProvided(game.getTeamHome().getId()) || pJoinApprovedCommand.getTeamId().equals(game.getTeamHome().getId()));
        if (getServer().getMode() == ServerMode.FUMBBL) {
          getServer().getFumbblRequestProcessor().add(new FumbblRequestLoadTeam(pGameState, pJoinApprovedCommand.getCoach(), pJoinApprovedCommand.getTeamId(), homeTeam, pJoinApprovedCommand.getSender()));
        } else {
          Team team = getServer().getGameCache().getTeamById(pJoinApprovedCommand.getTeamId());
          getServer().getGameCache().addTeamToGame(pGameState, team, homeTeam);
          if (UtilStartGame.joinGameAsPlayerAndCheckIfReadyToStart(pGameState, pJoinApprovedCommand.getSender(), pJoinApprovedCommand.getCoach(), homeTeam)) {
            UtilStartGame.startGame(pGameState);
          }
        }
      }
    }
  }
  
  private boolean checkForLoggedInCoach(GameState pGameState, String pCoach, SocketChannel pLoginChannel) {
    ChannelManager channelManager = getServer().getChannelManager(); 
    SocketChannel[] receivers = channelManager.getChannelsForGameId(pGameState.getId());
    for (int i = 0; i < receivers.length; i++) {
      if (pLoginChannel != receivers[i]) {
        String coach = channelManager.getCoachForChannel(receivers[i]);
        if (pCoach.equalsIgnoreCase(coach)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private GameState loadGameStateById(InternalServerCommandJoinApproved pJoinApprovedCommand) {
    GameCache gameCache = getServer().getGameCache();
    GameState gameState = gameCache.getGameStateById(pJoinApprovedCommand.getGameId());
    if (gameState == null) {
      if (getServer().getMode() == ServerMode.FUMBBL) {
        getServer().getFumbblRequestProcessor().add(
        	new FumbblRequestLoadGame(pJoinApprovedCommand.getGameId(), pJoinApprovedCommand.getCoach(), pJoinApprovedCommand.getTeamId(), pJoinApprovedCommand.getClientMode(), pJoinApprovedCommand.getSender())
        );
      } else {
				gameState = gameCache.queryFromDb(pJoinApprovedCommand.getGameId());
      }
    }
    return gameState;
  }
  
  private void sendTeamList(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand) {
    if (getServer().getMode() == ServerMode.FUMBBL) {
      getServer().getFumbblRequestProcessor().add(new FumbblRequestLoadTeamList(pGameState, pJoinApprovedCommand.getCoach(), pJoinApprovedCommand.getSender()));
    } else {
      TeamList teamList = new TeamList();
      Team[] teams = getServer().getGameCache().getTeamsForCoach(pJoinApprovedCommand.getCoach());
      for (Team team : teams) {
        TeamListEntry teamEntry = new TeamListEntry();
        teamEntry.init(team);
        teamList.add(teamEntry);
      }
      getServer().getCommunication().sendTeamList(pJoinApprovedCommand.getSender(), teamList);
    }
  }
  
}
