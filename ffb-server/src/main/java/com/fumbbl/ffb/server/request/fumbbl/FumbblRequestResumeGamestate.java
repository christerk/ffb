package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.ServerUrlProperty;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class FumbblRequestResumeGamestate extends ServerRequest {

	private final GameState fGameState;

	public FumbblRequestResumeGamestate(GameState pGameState) {
		fGameState = pGameState;
	}

	public GameState getGameState() {
		return fGameState;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		FantasyFootballServer server = pRequestProcessor.getServer();
		String challengeResponse = UtilFumbblRequest.getFumbblAuthChallengeResponseForFumbblUser(server);
		Game game = getGameState().getGame();
		if (!game.isTesting()) {
			GameResult gameResult = game.getGameResult();
			int spectators = getGameState().getServer().getSessionManager().getSessionsOfSpectators(game.getId()).length;
			setRequestUrl(StringTool.bind(ServerUrlProperty.FUMBBL_GAMESTATE_RESUME.url(server.getProperties()),
					new Object[] { challengeResponse, game.getId(), game.getTeamHome().getId(), game.getTeamAway().getId(),
							game.getHalf(), game.getTurnData().getTurnNr(), gameResult.getTeamResultHome().getScore(),
							gameResult.getTeamResultAway().getScore(), spectators }));
			server.getDebugLog().log(IServerLogLevel.INFO, game.getId(), DebugLog.FUMBBL_REQUEST, getRequestUrl());
			FumbblGameState fumbblGameState = UtilFumbblRequest.processFumbblGameStateRequest(server, getRequestUrl());
			if ((fumbblGameState == null) || !fumbblGameState.isOk()) {
				UtilFumbblRequest.reportFumbblError(getGameState(), fumbblGameState);
			}
		}
	}

}
