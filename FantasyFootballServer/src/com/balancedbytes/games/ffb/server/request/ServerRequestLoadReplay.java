package com.balancedbytes.games.ffb.server.request;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCacheMode;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandDeleteGame;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandReplayLoaded;
import com.balancedbytes.games.ffb.server.util.UtilHttpClient;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ServerRequestLoadReplay extends ServerRequest {

  private long fGameId;
  private int fReplayToCommandNr;
  private Session fSession;
  private boolean fCheckForDeletion;

  public ServerRequestLoadReplay(long pGameId, boolean pCheckForDeletion) {
    this(pGameId, 0, null, pCheckForDeletion);
  }

  public ServerRequestLoadReplay(long pGameId, int pReplayToCommandNr, Session pSession) {
    this(pGameId, pReplayToCommandNr, pSession, false);
  }

  private ServerRequestLoadReplay(long pGameId, int pReplayToCommandNr, Session pSession, boolean pCheckForDeletion) {
    fGameId = pGameId;
    fReplayToCommandNr = pReplayToCommandNr;
    fSession = pSession;
    fCheckForDeletion = pCheckForDeletion;
  }

  public long getGameId() {
    return fGameId;
  }
  
  public Session getSession() {
    return fSession;
  }
  
  public int getReplayToCommandNr() {
    return fReplayToCommandNr;
  }
  
  public boolean isCheckForDeletion() {
    return fCheckForDeletion;
  }
  
  @Override
  public void process(ServerRequestProcessor pRequestProcessor) {
    FantasyFootballServer server = pRequestProcessor.getServer();
    GameState gameState = null;
    try {
      String loadUrl = StringTool.bind(server.getProperty(IServerProperty.BACKUP_URL_LOAD), getGameId());
      byte[] gzippedJson = UtilHttpClient.fetchGzippedPage(loadUrl);
      JsonValue jsonValue = UtilJson.gunzip(gzippedJson);
      if ((jsonValue != null) && !jsonValue.isNull()) {
        gameState = new GameState(server);
        gameState.initFrom(jsonValue);
      }
    } catch (IOException pIoException) {
      server.getDebugLog().log(getGameId(), new FantasyFootballException("Unable to load Replay", pIoException));
      return;
    }
    if (gameState != null) {
      if (isCheckForDeletion()) {
        server.getCommunication().handleCommand(new InternalServerCommandDeleteGame(getGameId(), false));
      } else {
        server.getGameCache().add(gameState, GameCacheMode.REPLAY_GAME);
        InternalServerCommandReplayLoaded replayLoadedCommand = new InternalServerCommandReplayLoaded(getGameId(), getReplayToCommandNr());
        server.getCommunication().handleCommand(new ReceivedCommand(replayLoadedCommand, getSession()));
      }
    }
  }
  
}
