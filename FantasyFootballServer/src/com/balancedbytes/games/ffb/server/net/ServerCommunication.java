package com.balancedbytes.games.ffb.server.net;

import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.GameList;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.TeamList;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.change.ModelChangeList;
import com.balancedbytes.games.ffb.net.INetCommandHandler;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.net.commands.ServerCommand;
import com.balancedbytes.games.ffb.net.commands.ServerCommandAddPlayer;
import com.balancedbytes.games.ffb.net.commands.ServerCommandAdminMessage;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandGameState;
import com.balancedbytes.games.ffb.net.commands.ServerCommandJoin;
import com.balancedbytes.games.ffb.net.commands.ServerCommandLeave;
import com.balancedbytes.games.ffb.net.commands.ServerCommandModelSync;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPasswordChallenge;
import com.balancedbytes.games.ffb.net.commands.ServerCommandPing;
import com.balancedbytes.games.ffb.net.commands.ServerCommandRemovePlayer;
import com.balancedbytes.games.ffb.net.commands.ServerCommandSound;
import com.balancedbytes.games.ffb.net.commands.ServerCommandStatus;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTalk;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTeamList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandTeamSetupList;
import com.balancedbytes.games.ffb.net.commands.ServerCommandUserSettings;
import com.balancedbytes.games.ffb.net.commands.ServerCommandVersion;
import com.balancedbytes.games.ffb.report.ReportList;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommand;
import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommunication implements Runnable, INetCommandHandler {
  
  private boolean fStopped;
  private List<NetCommand> fQueue;
  private FantasyFootballServer fServer;
  
  public ServerCommunication(FantasyFootballServer pServer) {
    fServer = pServer;
    fQueue = Collections.synchronizedList(new LinkedList<NetCommand>()); 
  }
  
  public void handleNetCommand(NetCommand pNetCommand) {
    synchronized (fQueue) {
      fQueue.add(pNetCommand);
      fQueue.notify();
    }
  }
  
  public void run() {
    
    try {
    
      while (true) {
        
        NetCommand netCommand = null;
        synchronized (fQueue) {
          try {
            while (fQueue.isEmpty() && !fStopped) {
              fQueue.wait();
            }
          } catch (InterruptedException e) {
            break;
          }
          if (fStopped) {
            break;
          }
          netCommand = fQueue.remove(0);
        }
              
        getServer().getDebugLog().logClientCommand(IServerLogLevel.INFO, netCommand);
        try {
          getServer().getCommandHandlerFactory().handleNetCommand(netCommand);
        } catch (Exception any) {
          GameState gameState = null;

          // Fetch the game state if available
          try {
            long gameId = getServer().getChannelManager().getGameIdForChannel(netCommand.getSender());
            gameState = getServer().getGameCache().getGameStateById(gameId);
          } catch (Exception _) { }
          
          getServer().getDebugLog().log((gameState != null) ? gameState.getId() : -1, any);
          
          // Attempt to shut down the game.
          shutdownGame(gameState);
        }
        
        if ((netCommand != null) && (netCommand.getId() != NetCommandId.CLIENT_PING) && (netCommand.getId() != NetCommandId.CLIENT_DEBUG_CLIENT_STATE)) {
          long gameId = 0;
          if (netCommand.isInternal()) {
            gameId = ((InternalServerCommand) netCommand).getGameId();
          } else {
            gameId = getServer().getChannelManager().getGameIdForChannel(netCommand.getSender());
          }
          GameState gameState = getServer().getGameCache().getGameStateById(gameId);
          if (gameState != null) {
            try {
              if (netCommand.isInternal() || (getServer().getChannelManager().getChannelOfHomeCoach(gameState) == netCommand.getSender()) || (getServer().getChannelManager().getChannelOfAwayCoach(gameState) == netCommand.getSender())) {
                gameState.handleNetCommand(netCommand);
              }
            } catch (Exception any) {
              getServer().getDebugLog().log(any);
              shutdownGame(gameState);
            }
          }
        }
        
      }
      
    } catch (Exception pException) {
      getServer().getDebugLog().log(pException);
      System.exit(99);
    }
    
  }

  private void shutdownGame(GameState gameState) {
    
  	// Sanity checking
    if (gameState == null) {
      return;
    }
    
    // Send out an error message
    try {
      ServerCommandAdminMessage messageCommand = new ServerCommandAdminMessage(new String[] { "This match has entered an invalid state and is shutting down." });
      send(getServer().getChannelManager().getChannelsForGameId(gameState.getId()), messageCommand, false);
    } catch (Exception _) { }

    // Disconnect clients
    try {
      for (SocketChannel channel : getServer().getChannelManager().getChannelsForGameId(gameState.getId())) {
        getServer().getNioServer().removeChannel(channel);
      }
    } catch (Exception _) { }

  }
  
  public void stop() {
    fStopped = true;
    synchronized (fQueue) {
      fQueue.notifyAll();
    }
  }
  
  public FantasyFootballServer getServer() {
    return fServer;
  }
  
  public void send(SocketChannel pReceiver, NetCommand pNetCommand, boolean pLog) {
    if (pReceiver != null) {
      if (pLog) {
        getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pNetCommand, pReceiver);
      }
      send(new SocketChannel[] { pReceiver }, pNetCommand, false);
    }
  }

  protected void send(SocketChannel[] pReceivers, NetCommand pNetCommand, boolean pLog) {
    if (ArrayTool.isProvided(pReceivers)) {
      if (pLog) {
        getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pNetCommand, pReceivers);
      }
      for (int i = 0; i < pReceivers.length; i++) {
        getServer().getNioServer().send(pReceivers[i], pNetCommand);
      }
    }
  }
  
  protected void sendAllChannels(GameState pGameState, NetCommand pNetCommand) {
    if ((pGameState != null) && (pNetCommand != null)) {
      getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pNetCommand, DebugLog.COMMAND_SERVER_ALL_CLIENTS);
      if (pGameState != null) {
        ChannelManager channelManager = getServer().getChannelManager();
        SocketChannel[] allChannels = channelManager.getChannelsForGameId(pGameState.getId());
        send(allChannels, pNetCommand, false);
        pGameState.getGameLog().add((ServerCommand) pNetCommand);
      }
    }
  }

  protected void sendHomeChannel(GameState pGameState, NetCommand pNetCommand) {
    if ((pGameState != null) && (pNetCommand != null)) {
      getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pNetCommand, DebugLog.COMMAND_SERVER_HOME);
      ChannelManager channelManager = getServer().getChannelManager();
      SocketChannel homeChannel = channelManager.getChannelOfHomeCoach(pGameState);
      send(homeChannel, pNetCommand, false);
    }
  }

  protected void sendHomeAndSpectatorChannels(GameState pGameState, NetCommand pNetCommand) {
    if ((pGameState != null) && (pNetCommand != null)) {
      getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pNetCommand, DebugLog.COMMAND_SERVER_HOME_SPECTATORS);
      ChannelManager channelManager = getServer().getChannelManager();
      SocketChannel[] homeChannels = channelManager.getChannelsWithoutAwayCoach(pGameState);
      send(homeChannels, pNetCommand, false);
      pGameState.getGameLog().add((ServerCommand) pNetCommand);
    }
  }

  protected void sendAwayChannel(GameState pGameState, NetCommand pNetCommand) {
    if ((pGameState != null) && (pNetCommand != null)) {
      getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pNetCommand, DebugLog.COMMAND_SERVER_AWAY);
      ChannelManager channelManager = getServer().getChannelManager();
      SocketChannel awayChannel = channelManager.getChannelOfAwayCoach(pGameState);
      send(awayChannel, pNetCommand, false);
    }
  }
  
  protected void sendAwayAndSpectatorChannels(GameState pGameState, NetCommand pNetCommand) {
    if ((pGameState != null) && (pNetCommand != null)) {
      getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pNetCommand, DebugLog.COMMAND_SERVER_AWAY_SPECTATORS);
      ChannelManager channelManager = getServer().getChannelManager();
      SocketChannel[] spectatorChannels = channelManager.getChannelsWithoutHomeCoach(pGameState);
      send(spectatorChannels, pNetCommand, false);
      pGameState.getGameLog().add((ServerCommand) pNetCommand);
    }
  }

  protected void sendSpectatorChannels(GameState pGameState, NetCommand pNetCommand) {
    if ((pGameState != null) && (pNetCommand != null)) {
      getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pNetCommand, DebugLog.COMMAND_SERVER_SPECTATOR);
      ChannelManager channelManager = getServer().getChannelManager();
      SocketChannel[] spectatorChannels = channelManager.getChannelsOfSpectators(pGameState);
      send(spectatorChannels, pNetCommand, false);
      pGameState.getGameLog().add((ServerCommand) pNetCommand);
    }
  }

  // Server Commands
  
  public void sendUserSettings(SocketChannel pReceiver, String[] pSettingNames, String[] pSettingValues) {
    ServerCommandUserSettings userSettingsCommand = new ServerCommandUserSettings(pSettingNames, pSettingValues);
    send(pReceiver, userSettingsCommand, true);
    // not logged in Game Log
  }
  
  public void sendStatus(SocketChannel pReceiver, ServerStatus pStatus, String pMessage) {
    sendStatus(new SocketChannel[] { pReceiver }, pStatus, pMessage);
    // not logged in Game Log
  }
  
  public void sendStatus(SocketChannel[] pReceivers, ServerStatus pStatus, String pMessage) {
    ServerCommandStatus statusCommand = new ServerCommandStatus(pStatus, pMessage);
    send(pReceivers, statusCommand, true);
  }

  public void sendAdminMessage(String[] pMessages) {
    ServerCommandAdminMessage messageCommand = new ServerCommandAdminMessage(pMessages);
    ChannelManager channelManager = getServer().getChannelManager();
    SocketChannel[] allChannels = channelManager.getAllChannels();
    send(allChannels, messageCommand, false);
  }
  
  public void sendStatus(GameState pGameState, ServerStatus pStatus, String pMessage) {
    ServerCommandStatus statusCommand = new ServerCommandStatus(pStatus, pMessage);
    statusCommand.setCommandNr(pGameState.generateCommandNr());
    sendAllChannels(pGameState, statusCommand);
  }

  public void sendTeamList(SocketChannel pReceiver, TeamList pTeamList) {
    ServerCommandTeamList teamListCommand = new ServerCommandTeamList(pTeamList);
    send(pReceiver, teamListCommand, true);
    // not logged in Game Log
  }
  
  public void sendGameList(SocketChannel pReceiver, GameList pGameList) {
    ServerCommandGameList gameListCommand = new ServerCommandGameList(pGameList);
    send(pReceiver, gameListCommand, true);
    // not logged in Game Log
  }

  public void sendPasswordChallenge(SocketChannel pReceiver, String pChallenge) {
    ServerCommandPasswordChallenge passwordChallengeCommand = new ServerCommandPasswordChallenge(pChallenge);
    send(pReceiver, passwordChallengeCommand, true);
    // not logged in Game Log
  }

  public void sendVersion(SocketChannel pReceiver, String pServerVersion, String pClientVersion, String[] pClientProperties, String[] pClientPropertyValues) {
    ServerCommandVersion versionCommand = new ServerCommandVersion(pServerVersion, pClientVersion, pClientProperties, pClientPropertyValues); 
    send(pReceiver, versionCommand, true);
    // not logged in Game Log
  }

  public void sendJoin(SocketChannel[] pReceivers, String pCoach, ClientMode pMode, String[] pPlayers, int pSpectators) {
    ServerCommandJoin joinCommand = new ServerCommandJoin(pCoach, pMode, pPlayers, pSpectators);
    send(pReceivers, joinCommand, true);
    // not logged in Game Log
  }

  public void sendLeave(SocketChannel[] pReceivers, String pCoach, ClientMode pMode, int pSpectators) {
    ServerCommandLeave leaveCommand = new ServerCommandLeave(pCoach, pMode, pSpectators);
    send(pReceivers, leaveCommand, true);
    // not logged in Game Log
  }
  
  public void sendPing(SocketChannel pReceiver, long pClientTime) {
    ServerCommandPing pingCommand = new ServerCommandPing(pClientTime);
    send(pReceiver, pingCommand, false);
    // not logged in Game Log
  }

  public void sendGameState(SocketChannel pReceiver, GameState pGameState) {
    ServerCommandGameState gameStateCommand = new ServerCommandGameState(pGameState.getGame());
    send(pReceiver, gameStateCommand, true);
    // not logged in Game Log
  }
  
  public void sendTalk(SocketChannel pReceiver, GameState pGameState, String pCoach, String[] pTalk) {
    ServerCommandTalk talkCommand = new ServerCommandTalk(pCoach, pTalk);
    send(pReceiver, talkCommand, true);
    // not logged in Game Log
  }
    
  public void sendPlayerTalk(GameState pGameState, String pCoach, String pTalk) {
    ServerCommandTalk talkCommand = new ServerCommandTalk(pCoach, pTalk);
    sendAllChannels(pGameState, talkCommand);
    // not logged in Game Log
  }

  public void sendSpectatorTalk(GameState pGameState, String pCoach, String pTalk) {
    ServerCommandTalk talkCommand = new ServerCommandTalk(pCoach, pTalk);
    sendSpectatorChannels(pGameState, talkCommand);    // not logged in Game Log
  }

  public void sendTeamSetupList(SocketChannel pReceiver, String[] pSetupNames) {
    ServerCommandTeamSetupList teamSetupListCommand = new ServerCommandTeamSetupList(pSetupNames);
    send(pReceiver, teamSetupListCommand, true);
    // not logged in Game Log
  }
            
  public void sendGameState(GameState pGameState) {
    ServerCommandGameState gameStateCommand = new ServerCommandGameState(pGameState.getGame());
    sendHomeAndSpectatorChannels(pGameState, gameStateCommand);
    sendAwayChannel(pGameState, gameStateCommand.transform());
  }
  
  public void sendAddPlayer(GameState pGameState, String pTeamId, Player pPlayer, PlayerState pPlayerState, PlayerResult pPlayerResult) {
    ServerCommandAddPlayer addPlayersCommand = new ServerCommandAddPlayer(pTeamId, pPlayer, pPlayerState, pPlayerResult);
    addPlayersCommand.setCommandNr(pGameState.generateCommandNr());
    sendAllChannels(pGameState, addPlayersCommand);
  }
  
  public void sendRemovePlayer(GameState pGameState, String pPlayerId) {
    ServerCommandRemovePlayer removePlayerCommand = new ServerCommandRemovePlayer(pPlayerId);
    removePlayerCommand.setCommandNr(pGameState.generateCommandNr());
    sendAllChannels(pGameState, removePlayerCommand);
  }

  public void sendSound(GameState pGameState, Sound pSound) {
    ServerCommandSound soundCommand = new ServerCommandSound(pSound);
    soundCommand.setCommandNr(pGameState.generateCommandNr());
    sendAllChannels(pGameState, soundCommand);
  }
  
  public void sendModelSync(GameState pGameState, ModelChangeList pModelChanges, ReportList pReports, Animation pAnimation, Sound pSound, long pGameTime, long pTurnTime) {
    ServerCommandModelSync syncCommand = new ServerCommandModelSync(pModelChanges, pReports, pAnimation, pSound, pGameTime, pTurnTime);
    syncCommand.setCommandNr(pGameState.generateCommandNr());
    sendHomeAndSpectatorChannels(pGameState, syncCommand);
    sendAwayChannel(pGameState, syncCommand.transform());
  }
  
}
