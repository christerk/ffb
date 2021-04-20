package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandFumbblGameCreated;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class FumbblRequestCreateGamestate extends ServerRequest {

	private GameState fGameState;

	public FumbblRequestCreateGamestate(GameState pGameState) {
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
			setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_GAMESTATE_CREATE),
					new Object[] { challengeResponse, game.getId(), game.getTeamHome().getId(), game.getTeamAway().getId() }));
			server.getDebugLog().log(IServerLogLevel.INFO, game.getId(), DebugLog.FUMBBL_REQUEST, getRequestUrl());
			FumbblGameState fumbblGameState = UtilFumbblRequest.processFumbblGameStateRequest(server, getRequestUrl());
			if ((fumbblGameState == null) || !fumbblGameState.isOk()) {
				UtilFumbblRequest.reportFumbblError(getGameState(), fumbblGameState);
			} else {
				InternalServerCommandFumbblGameCreated gameCreatedCommand = new InternalServerCommandFumbblGameCreated(
						game.getId());
				server.getCommunication().handleCommand(gameCreatedCommand);
			}
		} else {
			InternalServerCommandFumbblGameCreated gameCreatedCommand = new InternalServerCommandFumbblGameCreated(
					game.getId());
			server.getCommunication().handleCommand(gameCreatedCommand);
		}
	}

}
