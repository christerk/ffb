package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandFumbblTeamLoaded;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestCheckGamestate;
import com.fumbbl.ffb.server.util.UtilServerStartGame;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerFumbblTeamLoaded extends ServerCommandHandler {

	protected ServerCommandHandlerFumbblTeamLoaded(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_FUMBBL_TEAM_LOADED;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {
		InternalServerCommandFumbblTeamLoaded teamLoadedCommand = (InternalServerCommandFumbblTeamLoaded) pReceivedCommand
				.getCommand();
		GameState gameState = getServer().getGameCache().getGameStateById(teamLoadedCommand.getGameId());
		if (gameState == null) {
			return false;
		}
		if (UtilServerStartGame.joinGameAsPlayerAndCheckIfReadyToStart(gameState, pReceivedCommand.getSession(),
				teamLoadedCommand.getCoach(), teamLoadedCommand.isHomeTeam())) {
			getServer().getRequestProcessor().add(new FumbblRequestCheckGamestate(gameState));
		}
		return true;
	}

}
