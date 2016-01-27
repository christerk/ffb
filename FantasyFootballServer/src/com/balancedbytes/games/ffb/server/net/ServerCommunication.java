package com.balancedbytes.games.ffb.server.net;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.GameList;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.TeamList;
import com.balancedbytes.games.ffb.json.LZString;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.change.ModelChangeList;
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
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.handler.IReceivedCommandHandler;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandSocketClosed;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommunication implements Runnable, IReceivedCommandHandler {
  
  private boolean fStopped;
  private List<ReceivedCommand> fReceiveQueue;
  private FantasyFootballServer fServer;
  private boolean fCommandCompression;
  
  public ServerCommunication(FantasyFootballServer pServer) {
    fServer = pServer;
    fReceiveQueue = Collections.synchronizedList(new LinkedList<ReceivedCommand>());
    String commandCompression = (fServer != null) ? fServer.getProperty(IServerProperty.SERVER_COMMAND_COMPRESSION) : null; 
    if (StringTool.isProvided(commandCompression)) {
      fCommandCompression = Boolean.parseBoolean(commandCompression);
    }
  }
  
  public void handleCommand(ReceivedCommand pReceivedCommand) {
    synchronized (fReceiveQueue) {
      fReceiveQueue.add(pReceivedCommand);
      fReceiveQueue.notify();
    }
  }
  
  public void handleCommand(InternalServerCommand pInternalServerCommand) {
    handleCommand(new ReceivedCommand(pInternalServerCommand, null));
  }
  
  public void run() {
    
    try {
    
      while (true) {
        
        ReceivedCommand receivedCommand = null;
        synchronized (fReceiveQueue) {
          try {
            while (fReceiveQueue.isEmpty() && !fStopped) {
              fReceiveQueue.wait();
            }
          } catch (InterruptedException e) {
            break;
          }
          if (fStopped) {
            break;
          }
          receivedCommand = fReceiveQueue.remove(0);
        }
              
        getServer().getDebugLog().logClientCommand(IServerLogLevel.INFO, receivedCommand);
        try {
          getServer().getCommandHandlerFactory().handleCommand(receivedCommand);
        } catch (Exception any) {
          GameState gameState = null;

          // Fetch the game state if available
          try {
            long gameId = getServer().getSessionManager().getGameIdForSession(receivedCommand.getSession());
            gameState = getServer().getGameCache().getGameStateById(gameId);
          } catch (Exception _) { }
          
          getServer().getDebugLog().log((gameState != null) ? gameState.getId() : -1, any);
          
          // Attempt to shut down the game.
          shutdownGame(gameState);
        }
        
        if ((receivedCommand != null) && (receivedCommand.getId() != NetCommandId.CLIENT_PING) && (receivedCommand.getId() != NetCommandId.CLIENT_DEBUG_CLIENT_STATE)) {
          long gameId = 0;
          if (receivedCommand.isInternal()) {
            gameId = ((InternalServerCommand) receivedCommand.getCommand()).getGameId();
          } else {
            gameId = getServer().getSessionManager().getGameIdForSession(receivedCommand.getSession());
          }
          GameState gameState = getServer().getGameCache().getGameStateById(gameId);
          if (gameState != null) {
            try {
              if (receivedCommand.isInternal() || (getServer().getSessionManager().getSessionOfHomeCoach(gameState) == receivedCommand.getSession()) || (getServer().getSessionManager().getSessionOfAwayCoach(gameState) == receivedCommand.getSession())) {
                gameState.handleCommand(receivedCommand);
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
      send(getServer().getSessionManager().getSessionsForGameId(gameState.getId()), messageCommand, false);
    } catch (Exception _) { }

    // Disconnect clients
    try {
      for (Session session : getServer().getSessionManager().getSessionsForGameId(gameState.getId())) {
        getServer().getCommunication().close(session);
      }
    } catch (Exception _) { }

  }
  
  public void stop() {
    fStopped = true;
    synchronized (fReceiveQueue) {
      fReceiveQueue.notifyAll();
    }
  }
  
  public FantasyFootballServer getServer() {
    return fServer;
  }
  
  public void close(Session pSession) {
    if (pSession == null) {
      return;
    }
    pSession.close();
    handleCommand(new ReceivedCommand(new InternalServerCommandSocketClosed(), pSession));
  }

  public void send(Session pSession, NetCommand pCommand, boolean pLog) {
    if (pLog && (pSession != null) && (pCommand != null)) {
      getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pCommand, pSession);
    }
    send(pSession, pCommand);
  }
  
  protected void send(Session[] pSessions, NetCommand pCommand, boolean pLog) {
    if (pLog && ArrayTool.isProvided(pSessions) && (pCommand != null)) {
      getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pCommand, pSessions);
    }
    for (int i = 0; i < pSessions.length; i++) {
      send(pSessions[i], pCommand);
    }
  }
  
  private Future<Void> send(Session pSession, NetCommand pCommand) {
    
    if ((pSession == null) || (pCommand == null) || !pSession.isOpen()) {
      return null;
    }

    JsonValue jsonValue = pCommand.toJsonValue();
    if (jsonValue == null) {
      return null;
    }
    
    String textMessage = jsonValue.toString();
    if (fCommandCompression) {
      textMessage = LZString.compressToUTF16(textMessage);
    }
    
    if (!StringTool.isProvided(textMessage)) {
      return null;
    }
    
    try {
      return pSession.getRemote().sendStringByFuture(textMessage);
    } catch (WebSocketException pWebSocketException) {
      // getServer().getDebugLog().log(IServerLogLevel.WARN, pWebSocketException.getMessage());
      close(pSession);
    }

    return null;

  }

  protected void sendAllSessions(GameState pGameState, NetCommand pCommand) {
    if ((pGameState == null) || (pCommand == null)) {
      return;
    }
    getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pCommand, DebugLog.COMMAND_SERVER_ALL_CLIENTS);
    if (pGameState != null) {
      SessionManager sessionManager = getServer().getSessionManager();
      Session[] allSessions = sessionManager.getSessionsForGameId(pGameState.getId());
      send(allSessions, pCommand, false);
      pGameState.getGameLog().add((ServerCommand) pCommand);
    }
  }

  protected void sendHomeSession(GameState pGameState, NetCommand pCommand) {
    if ((pGameState == null) || (pCommand == null)) {
      return;
    }
    getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pCommand, DebugLog.COMMAND_SERVER_HOME);
    SessionManager sessionManager = getServer().getSessionManager();
    Session homeSession = sessionManager.getSessionOfHomeCoach(pGameState);
    send(homeSession, pCommand, false);
  }

  protected void sendHomeAndSpectatorSessions(GameState pGameState, NetCommand pCommand) {
    if ((pGameState == null) || (pCommand == null)) {
      return;
    }
    getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pCommand, DebugLog.COMMAND_SERVER_HOME_SPECTATORS);
    SessionManager sessionManager = getServer().getSessionManager();
    Session[] sessions = sessionManager.getSessionsWithoutAwayCoach(pGameState);
    send(sessions, pCommand, false);
    pGameState.getGameLog().add((ServerCommand) pCommand);
  }

  protected void sendAwaySession(GameState pGameState, NetCommand pCommand) {
    if ((pGameState == null) || (pCommand == null)) {
      return;
    }
    getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pCommand, DebugLog.COMMAND_SERVER_AWAY);
    SessionManager sessionManager = getServer().getSessionManager();
    Session awaySession = sessionManager.getSessionOfAwayCoach(pGameState);
    send(awaySession, pCommand, false);
  }
  
  protected void sendAwayAndSpectatorSessions(GameState pGameState, NetCommand pCommand) {
    if ((pGameState == null) || (pCommand == null)) {
      return;
    }
    getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pCommand, DebugLog.COMMAND_SERVER_AWAY_SPECTATORS);
    SessionManager sessionManager = getServer().getSessionManager();
    Session[] sessions = sessionManager.getSessionsWithoutHomeCoach(pGameState);
    send(sessions, pCommand, false);
    pGameState.getGameLog().add((ServerCommand) pCommand);
  }

  protected void sendSpectatorSessions(GameState pGameState, NetCommand pCommand) {
    if ((pGameState == null) || (pCommand == null)) {
      return;
    }
    getServer().getDebugLog().logServerCommand(IServerLogLevel.INFO, pGameState.getId(), pCommand, DebugLog.COMMAND_SERVER_SPECTATOR);
    SessionManager sessionManager = getServer().getSessionManager();
    Session[] spectatorSessions = sessionManager.getSessionsOfSpectators(pGameState);
    send(spectatorSessions, pCommand, false);
    pGameState.getGameLog().add((ServerCommand) pCommand);
  }

  // Server Commands
  
  public void sendUserSettings(Session pSession, String[] pSettingNames, String[] pSettingValues) {
    ServerCommandUserSettings userSettingsCommand = new ServerCommandUserSettings(pSettingNames, pSettingValues);
    send(pSession, userSettingsCommand, true);
    // not logged in Game Log
  }
  
  public void sendStatus(Session pSession, ServerStatus pStatus, String pMessage) {
    sendStatus(new Session[] { pSession }, pStatus, pMessage);
    // not logged in Game Log
  }
  
  public void sendStatus(Session[] pSessions, ServerStatus pStatus, String pMessage) {
    ServerCommandStatus statusCommand = new ServerCommandStatus(pStatus, pMessage);
    send(pSessions, statusCommand, true);
  }

  public void sendAdminMessage(String[] pMessages) {
    ServerCommandAdminMessage messageCommand = new ServerCommandAdminMessage(pMessages);
    SessionManager sessionManager = getServer().getSessionManager();
    Session[] allSessions = sessionManager.getAllSessions();
    send(allSessions, messageCommand, false);
  }
  
  public void sendStatus(GameState pGameState, ServerStatus pStatus, String pMessage) {
    ServerCommandStatus statusCommand = new ServerCommandStatus(pStatus, pMessage);
    statusCommand.setCommandNr(pGameState.generateCommandNr());
    sendAllSessions(pGameState, statusCommand);
  }

  public void sendTeamList(Session pSession, TeamList pTeamList) {
    ServerCommandTeamList teamListCommand = new ServerCommandTeamList(pTeamList);
    send(pSession, teamListCommand, true);
    // not logged in Game Log
  }
  
  public void sendGameList(Session pSession, GameList pGameList) {
    ServerCommandGameList gameListCommand = new ServerCommandGameList(pGameList);
    send(pSession, gameListCommand, true);
    // not logged in Game Log
  }

  public void sendPasswordChallenge(Session pSession, String pChallenge) {
    ServerCommandPasswordChallenge passwordChallengeCommand = new ServerCommandPasswordChallenge(pChallenge);
    send(pSession, passwordChallengeCommand, true);
    // not logged in Game Log
  }

  public void sendVersion(Session pSession, String pServerVersion, String pClientVersion, String[] pClientProperties, String[] pClientPropertyValues) {
    ServerCommandVersion versionCommand = new ServerCommandVersion(pServerVersion, pClientVersion, pClientProperties, pClientPropertyValues); 
    send(pSession, versionCommand, true);
    // not logged in Game Log
  }

  public void sendJoin(Session[] pSessions, String pCoach, ClientMode pMode, String[] pPlayers, int pSpectators) {
    ServerCommandJoin joinCommand = new ServerCommandJoin(pCoach, pMode, pPlayers, pSpectators);
    send(pSessions, joinCommand, true);
    // not logged in Game Log
  }

  public void sendLeave(Session[] pSessions, String pCoach, ClientMode pMode, int pSpectators) {
    ServerCommandLeave leaveCommand = new ServerCommandLeave(pCoach, pMode, pSpectators);
    send(pSessions, leaveCommand, true);
    // not logged in Game Log
  }
  
  public void sendPing(Session pSession, long pClientTime) {
    ServerCommandPing pingCommand = new ServerCommandPing(pClientTime);
    send(pSession, pingCommand, false);
    // not logged in Game Log
  }

  public void sendGameState(Session pSession, GameState pGameState) {
    ServerCommandGameState gameStateCommand = new ServerCommandGameState(pGameState.getGame());
    send(pSession, gameStateCommand, true);
    // not logged in Game Log
  }
  
  public void sendTalk(Session pSession, GameState pGameState, String pCoach, String[] pTalk) {
    ServerCommandTalk talkCommand = new ServerCommandTalk(pCoach, pTalk);
    send(pSession, talkCommand, true);
    // not logged in Game Log
  }
    
  public void sendPlayerTalk(GameState pGameState, String pCoach, String pTalk) {
    ServerCommandTalk talkCommand = new ServerCommandTalk(pCoach, pTalk);
    sendAllSessions(pGameState, talkCommand);
    // not logged in Game Log
  }

  public void sendSpectatorTalk(GameState pGameState, String pCoach, String pTalk) {
    ServerCommandTalk talkCommand = new ServerCommandTalk(pCoach, pTalk);
    sendSpectatorSessions(pGameState, talkCommand);    // not logged in Game Log
  }

  public void sendTeamSetupList(Session pSession, String[] pSetupNames) {
    ServerCommandTeamSetupList teamSetupListCommand = new ServerCommandTeamSetupList(pSetupNames);
    send(pSession, teamSetupListCommand, true);
    // not logged in Game Log
  }
            
  public void sendGameState(GameState pGameState) {
    ServerCommandGameState gameStateCommand = new ServerCommandGameState(pGameState.getGame());
    sendHomeAndSpectatorSessions(pGameState, gameStateCommand);
    sendAwaySession(pGameState, gameStateCommand.transform());
  }
  
  public void sendAddPlayer(GameState pGameState, String pTeamId, Player pPlayer, PlayerState pPlayerState, PlayerResult pPlayerResult) {
    ServerCommandAddPlayer addPlayersCommand = new ServerCommandAddPlayer(pTeamId, pPlayer, pPlayerState, pPlayerResult);
    addPlayersCommand.setCommandNr(pGameState.generateCommandNr());
    sendAllSessions(pGameState, addPlayersCommand);
  }
  
  public void sendRemovePlayer(GameState pGameState, String pPlayerId) {
    ServerCommandRemovePlayer removePlayerCommand = new ServerCommandRemovePlayer(pPlayerId);
    removePlayerCommand.setCommandNr(pGameState.generateCommandNr());
    sendAllSessions(pGameState, removePlayerCommand);
  }

  public void sendSound(GameState pGameState, SoundId pSound) {
    ServerCommandSound soundCommand = new ServerCommandSound(pSound);
    soundCommand.setCommandNr(pGameState.generateCommandNr());
    sendAllSessions(pGameState, soundCommand);
  }
  
  public void sendModelSync(GameState pGameState, ModelChangeList pModelChanges, ReportList pReports, Animation pAnimation, SoundId pSound, long pGameTime, long pTurnTime) {
    ServerCommandModelSync syncCommand = new ServerCommandModelSync(pModelChanges, pReports, pAnimation, pSound, pGameTime, pTurnTime);
    syncCommand.setCommandNr(pGameState.generateCommandNr());
    sendHomeAndSpectatorSessions(pGameState, syncCommand);
    sendAwaySession(pGameState, syncCommand.transform());
  }
  
}
