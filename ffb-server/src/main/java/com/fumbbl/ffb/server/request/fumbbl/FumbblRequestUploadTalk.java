package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.ServerUrlProperty;
import com.fumbbl.ffb.server.Talk;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;

/**
 * @author Kalimar
 */
public class FumbblRequestUploadTalk extends ServerRequest {

	private final Talk talk;
	private final GameState gameState;

	public FumbblRequestUploadTalk(Talk talk, GameState gameState) {
		this.talk = talk;
		this.gameState = gameState;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		FantasyFootballServer server = pRequestProcessor.getServer();
		long gameId = gameState.getId();

		try {
			String challengeResponse = UtilFumbblRequest.getFumbblAuthChallengeResponseForFumbblUser(server);
			String chatJson = talk.toJsonValue().toString();
			server.getDebugLog().log(IServerLogLevel.DEBUG, gameId, chatJson);

			setRequestUrl(ServerUrlProperty.FUMBBL_TALK.url(server.getProperties()));
			server.getDebugLog().log(IServerLogLevel.DEBUG, gameId, DebugLog.FUMBBL_REQUEST, getRequestUrl() + " with payload " + chatJson);

			String response = UtilServerHttpClient.postAuthorizedForm(getRequestUrl(), challengeResponse, "chat", chatJson);
			server.getDebugLog().log(IServerLogLevel.DEBUG, gameId, response);

		} catch (Exception ex) {
			server.getDebugLog().log(gameId, ex);
		}

	}

}
