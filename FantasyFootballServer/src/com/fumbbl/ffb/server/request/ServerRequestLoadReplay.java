package com.fumbbl.ffb.server.request;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandDeleteGame;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandReplayLoaded;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandUploadGame;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;

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
	private String teamId;

	public ServerRequestLoadReplay(long pGameId, int pReplayToCommandNr, Session pSession, int pMode) {
		this(pGameId, pReplayToCommandNr, pSession, pMode, null);
	}

	public ServerRequestLoadReplay(long pGameId, int pReplayToCommandNr, Session pSession, int pMode, String teamId) {
		fGameId = pGameId;
		fReplayToCommandNr = pReplayToCommandNr;
		fSession = pSession;
		fMode = pMode;
		this.teamId = teamId;
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
			String jsonString = UtilServerHttpClient.fetchPage(loadUrl);
			if (StringTool.isProvided(jsonString)) {
				JsonValue jsonValue = JsonValue.readFrom(jsonString);
				if ((jsonValue != null) && !jsonValue.isNull()) {
					gameState = new GameState(server);
					gameState.initFrom(gameState.getGame().getRules(), jsonValue);
				}
			}
		} catch (ParseException parseException) {
			server.getDebugLog().log(getGameId(), new FantasyFootballException("Unable to load Replay", parseException));
			return;
		} catch (IOException ioException) {
			server.getDebugLog().log(getGameId(), new FantasyFootballException("Unable to load Replay", ioException));
			return;
		}
		if (fMode == LOAD_GAME) {
			if (gameState != null) {
				gameState.setStatus(GameStatus.LOADING);
				server.getGameCache().addGame(gameState);
				InternalServerCommandReplayLoaded replayLoadedCommand = new InternalServerCommandReplayLoaded(getGameId(),
						getReplayToCommandNr());
				server.getCommunication().handleCommand(new ReceivedCommand(replayLoadedCommand, getSession()));
			} else {
				server.getCommunication().sendStatus(getSession(), ServerStatus.REPLAY_UNAVAILABLE, "");
			}
		}
		if ((fMode == DELETE_GAME) && (gameState != null)) {
			server.getCommunication().handleCommand(new InternalServerCommandDeleteGame(getGameId(), false));
		}
		if ((fMode == UPLOAD_GAME) && (gameState != null)) {
			gameState.setStatus(GameStatus.LOADING);
			server.getGameCache().addGame(gameState);
			InternalServerCommandUploadGame uploadCommand = new InternalServerCommandUploadGame(getGameId(), teamId);
			server.getCommunication().handleCommand(new ReceivedCommand(uploadCommand, getSession()));
		}
	}

}
