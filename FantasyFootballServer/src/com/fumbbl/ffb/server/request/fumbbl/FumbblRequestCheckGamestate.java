package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandFumbblGameChecked;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class FumbblRequestCheckGamestate extends ServerRequest {

	private final GameState fGameState;

	public FumbblRequestCheckGamestate(GameState pGameState) {
		fGameState = pGameState;
	}

	public GameState getGameState() {
		return fGameState;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		Game game = getGameState().getGame();
		FantasyFootballServer server = pRequestProcessor.getServer();
		if (game.isTesting()) {

			setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_GAMESTATE_OPTIONS),
				new Object[]{game.getTeamHome().getId(), game.getTeamAway().getId()}));
			server.getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), DebugLog.FUMBBL_REQUEST, getRequestUrl());
			FumbblGameState fumbblGameState = UtilFumbblRequest.processFumbblGameStateRequest(server, getRequestUrl());
			game.getOptions().init(fumbblGameState.getOptions());
			server.getDebugLog().log(IServerLogLevel.TRACE, getGameState().getId(),
				game.getOptions().toJsonValue().toString());

			InternalServerCommandFumbblGameChecked gameCheckedCommand = new InternalServerCommandFumbblGameChecked(
				getGameState().getId());
			server.getCommunication().handleCommand(gameCheckedCommand);
		} else {
			setRequestUrl(StringTool.bind(server.getProperty(IServerProperty.FUMBBL_GAMESTATE_CHECK),
				new Object[]{game.getTeamHome().getId(), game.getTeamAway().getId()}));
			server.getDebugLog().log(IServerLogLevel.DEBUG, game.getId(), DebugLog.FUMBBL_REQUEST, getRequestUrl());
			FumbblGameState fumbblGameState = UtilFumbblRequest.processFumbblGameStateRequest(server, getRequestUrl());
			if ((fumbblGameState == null) || !fumbblGameState.isOk()) {
				UtilFumbblRequest.reportFumbblError(getGameState(), fumbblGameState);
			} else {
				game.getOptions().init(fumbblGameState.getOptions());
				server.getDebugLog().log(IServerLogLevel.TRACE, getGameState().getId(),
					game.getOptions().toJsonValue().toString());
				game.setTesting(game.isTesting() || UtilGameOption.isOptionEnabled(game, GameOptionId.TEST_MODE));
				InternalServerCommandFumbblGameChecked gameCheckedCommand = new InternalServerCommandFumbblGameChecked(
						getGameState().getId());
				server.getCommunication().handleCommand(gameCheckedCommand);
			}
		}
	}

}
