package com.balancedbytes.games.ffb.server.handler;

import java.nio.channels.SocketChannel;

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
import com.balancedbytes.games.ffb.server.GameCacheMode;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestCheckGamestate;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestLoadGame;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestLoadTeam;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestLoadTeamList;
import com.balancedbytes.games.ffb.server.net.ChannelManager;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
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

  public void handleCommand(ReceivedCommand pReceivedCommand) {
    
    InternalServerCommandJoinApproved joinApprovedCommand = (InternalServerCommandJoinApproved) pReceivedCommand.getCommand();
    ServerCommunication communication = getServer().getCommunication();
    ChannelManager channelManager = getServer().getChannelManager();
    GameCache gameCache = getServer().getGameCache();
    GameState gameState = null;
    SocketChannel sender = pReceivedCommand.getSender();

    if (joinApprovedCommand.getGameId() > 0) {
      gameState = loadGameStateById(joinApprovedCommand, sender);
            
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
            joinWithoutTeam(gameState, joinApprovedCommand, sender);
          } else {
            if (StringTool.isProvided(joinApprovedCommand.getTeamId())) {
              joinWithTeam(gameState, joinApprovedCommand, sender);
            } else {
              sendTeamList(gameState, joinApprovedCommand, sender);
            }
          }

        } else if (game.getStarted() != null) {
          communication.sendStatus(pReceivedCommand.getSender(), ServerStatus.ERROR_GAME_IN_USE, null);
          
        } else if (!StringTool.isProvided(joinApprovedCommand.getTeamId())) {
          sendTeamList(gameState, joinApprovedCommand, sender);

        } else {
          joinWithTeam(gameState, joinApprovedCommand, sender);
        }
      
      // ClientMode.SPECTATOR
      } else {
        
        if (checkForLoggedInCoach(gameState, joinApprovedCommand.getCoach(), pReceivedCommand.getSender())) {
          getServer().getCommunication().sendStatus(sender, ServerStatus.ERROR_ALREADY_LOGGED_IN, null);
        } else {
          channelManager.addChannel(sender, gameState, joinApprovedCommand.getCoach(), joinApprovedCommand.getClientMode(), false);
          UtilStartGame.sendServerJoin(gameState, sender, joinApprovedCommand.getCoach(), false, ClientMode.SPECTATOR);
          if (gameState.getGame().getStarted() != null) {
            UtilTimer.syncTime(gameState);
            communication.sendGameState(pReceivedCommand.getSender(), gameState);
          }
        }
        
      }
      
    }
    
  }
  
  private void joinWithoutTeam(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand, SocketChannel pSender) {
    Game game = pGameState.getGame();
    if (pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach()) || pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamAway().getCoach())) {
      if (!game.isTesting() && checkForLoggedInCoach(pGameState, pJoinApprovedCommand.getCoach(), pSender)) {
        getServer().getCommunication().sendStatus(pSender, ServerStatus.ERROR_ALREADY_LOGGED_IN, null);
      } else {
        boolean homeTeam = pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach());
        if (UtilStartGame.joinGameAsPlayerAndCheckIfReadyToStart(pGameState, pSender, pJoinApprovedCommand.getCoach(), homeTeam)) {
          if (getServer().getMode() == ServerMode.FUMBBL) {
            getServer().getFumbblRequestProcessor().add(new FumbblRequestCheckGamestate(pGameState));
          } else {
            UtilStartGame.startGame(pGameState);
          }
        }
      }
    }
  }
  
  private void joinWithTeam(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand, SocketChannel pSender) {
    if ((pGameState != null) && StringTool.isProvided(pJoinApprovedCommand.getTeamId())) {
      Game game = pGameState.getGame();
      if (!game.isTesting() && checkForLoggedInCoach(pGameState, pJoinApprovedCommand.getCoach(), pSender)) {
        getServer().getCommunication().sendStatus(pSender, ServerStatus.ERROR_ALREADY_LOGGED_IN, null);
      } else {
        boolean homeTeam = (!StringTool.isProvided(game.getTeamHome().getId()) || pJoinApprovedCommand.getTeamId().equals(game.getTeamHome().getId()));
        if (getServer().getMode() == ServerMode.FUMBBL) {
          getServer().getFumbblRequestProcessor().add(new FumbblRequestLoadTeam(pGameState, pJoinApprovedCommand.getCoach(), pJoinApprovedCommand.getTeamId(), homeTeam, pSender));
        } else {
          Team team = getServer().getGameCache().getTeamById(pJoinApprovedCommand.getTeamId());
          getServer().getGameCache().addTeamToGame(pGameState, team, homeTeam);
          if (UtilStartGame.joinGameAsPlayerAndCheckIfReadyToStart(pGameState, pSender, pJoinApprovedCommand.getCoach(), homeTeam)) {
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
  
  private GameState loadGameStateById(InternalServerCommandJoinApproved pJoinApprovedCommand, SocketChannel pSender) {
    GameCache gameCache = getServer().getGameCache();
    GameState gameState = gameCache.getGameStateById(pJoinApprovedCommand.getGameId());
    if (gameState == null) {
      if (getServer().getMode() == ServerMode.FUMBBL) {
        getServer().getFumbblRequestProcessor().add(
        	new FumbblRequestLoadGame(pJoinApprovedCommand.getGameId(), pJoinApprovedCommand.getCoach(), pJoinApprovedCommand.getTeamId(), pJoinApprovedCommand.getClientMode(), pSender)
        );
      } else {
				gameState = gameCache.queryFromDb(pJoinApprovedCommand.getGameId());
      }
    }
    return gameState;
  }
  
  private void sendTeamList(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand, SocketChannel pSender) {
    if (getServer().getMode() == ServerMode.FUMBBL) {
      getServer().getFumbblRequestProcessor().add(new FumbblRequestLoadTeamList(pGameState, pJoinApprovedCommand.getCoach(), pSender));
    } else {
      TeamList teamList = new TeamList();
      Team[] teams = getServer().getGameCache().getTeamsForCoach(pJoinApprovedCommand.getCoach());
      for (Team team : teams) {
        TeamListEntry teamEntry = new TeamListEntry();
        teamEntry.init(team);
        teamList.add(teamEntry);
      }
      getServer().getCommunication().sendTeamList(pSender, teamList);
    }
  }
  
}
