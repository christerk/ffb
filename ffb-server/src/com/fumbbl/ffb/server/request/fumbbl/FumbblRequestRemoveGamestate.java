package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class FumbblRequestRemoveGamestate extends ServerRequest {

	private GameState fGameState;

	public FumbblRequestRemoveGamestate(GameState pGameState) {
		fGameState = pGameState;
	}

	public GameState getGameState() {
		return fGameState;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		FantasyFootballServer server = pRequestProcessor.getServer();
		Game game = getGameState().getGame();
		String challengeResponse = UtilFumbblRequest.getFumbblAuthChallengeResponseForFumbblUser(server);
		setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_GAMESTATE_REMOVE),
				new Object[] { challengeResponse, game.getId() }));
		server.getDebugLog().log(IServerLogLevel.INFO, game.getId(), DebugLog.FUMBBL_REQUEST, getRequestUrl());
		FumbblGameState fumbblGameState = UtilFumbblRequest.processFumbblGameStateRequest(server, getRequestUrl());
		if ((fumbblGameState == null) || !fumbblGameState.isOk()) {
			UtilFumbblRequest.reportFumbblError(getGameState(), fumbblGameState);
		}
	}

}
