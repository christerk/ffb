package com.balancedbytes.games.ffb.server.request;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCacheMode;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandDeleteGame;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandReplayLoaded;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandUploadGame;
import com.balancedbytes.games.ffb.server.util.UtilServerHttpClient;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;


/**
 * 
 * @author Kalimar
 */
public class ServerRequestLoadReplay extends ServerRequest {

  public static final int LOAD_GAME = 1;
  public static final int DELETE_GAME = 2;
  public static final int UPLOAD_GAME = 3;  
  
  private long fGameId;
  private int fReplayToCommandNr;
  private Session fSession;
  private int fMode;

  public ServerRequestLoadReplay(long pGameId, int pReplayToCommandNr, Session pSession, int pMode) {
    fGameId = pGameId;
    fReplayToCommandNr = pReplayToCommandNr;
    fSession = pSession;
    fMode = pMode;
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
  
  @Override
  public void process(ServerRequestProcessor pRequestProcessor) {
    FantasyFootballServer server = pRequestProcessor.getServer();
    GameState gameState = null;
    try {
      String loadUrl = StringTool.bind(server.getProperty(IServerProperty.BACKUP_URL_LOAD), getGameId());
      JsonValue jsonValue = JsonValue.readFrom(UtilServerHttpClient.fetchPage(loadUrl));
      if ((jsonValue != null) && !jsonValue.isNull()) {
        gameState = new GameState(server);
        gameState.initFrom(jsonValue);
      }
    } catch (ParseException parseException) {
      server.getDebugLog().log(getGameId(), new FantasyFootballException("Unable to load Replay", parseException));
      return;
    } catch (IOException ioException) {
      server.getDebugLog().log(getGameId(), new FantasyFootballException("Unable to load Replay", ioException));
      return;
    }
    if (gameState != null) {
      if (fMode == LOAD_GAME) {
        server.getGameCache().add(gameState, GameCacheMode.REPLAY_GAME);
        InternalServerCommandReplayLoaded replayLoadedCommand = new InternalServerCommandReplayLoaded(getGameId(), getReplayToCommandNr());
        server.getCommunication().handleCommand(new ReceivedCommand(replayLoadedCommand, getSession()));
      }
      if (fMode == DELETE_GAME) {
        server.getCommunication().handleCommand(new InternalServerCommandDeleteGame(getGameId(), false));
      }
      if (fMode == UPLOAD_GAME) {
        server.getGameCache().add(gameState, GameCacheMode.REPLAY_GAME);
        InternalServerCommandUploadGame uploadCommand = new InternalServerCommandUploadGame(gameState.getId());
        server.getCommunication().handleCommand(new ReceivedCommand(uploadCommand, getSession()));
      }
    }
  }
  
}
