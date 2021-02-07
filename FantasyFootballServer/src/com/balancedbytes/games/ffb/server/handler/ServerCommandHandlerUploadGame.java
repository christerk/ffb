package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCache;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.factory.SequenceGeneratorFactory;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandUploadGame;
import com.balancedbytes.games.ffb.server.request.ServerRequestLoadReplay;
import com.balancedbytes.games.ffb.server.step.generator.EndGame;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerUploadGame extends ServerCommandHandler {

	protected ServerCommandHandlerUploadGame(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_UPLOAD_GAME;
	}

	public boolean handleCommand(ReceivedCommand receivedCommand) {
		InternalServerCommandUploadGame uploadGameCommand = (InternalServerCommandUploadGame) receivedCommand.getCommand();
		GameCache gameCache = getServer().getGameCache();
		GameState gameState = gameCache.getGameStateById(uploadGameCommand.getGameId());
		// GameState gameState = gameCache.closeGame(uploadGameCommand.getGameId());
		if (gameState == null) {
			// game has been moved out of the db - request it from the backup service
			getServer().getRequestProcessor().add(new ServerRequestLoadReplay(uploadGameCommand.getGameId(), 0,
					receivedCommand.getSession(), ServerRequestLoadReplay.UPLOAD_GAME, uploadGameCommand.getConcedingTeamId()));
		} else {
			gameState.getStepStack().clear();
			Game game = gameState.getGame();
			if (StringTool.isProvided(uploadGameCommand.getConcedingTeamId())) {
				game.getGameResult().getTeamResultHome()
						.setConceded(game.getTeamHome().getId().equals(uploadGameCommand.getConcedingTeamId()));
				game.getGameResult().getTeamResultAway()
						.setConceded(game.getTeamAway().getId().equals(uploadGameCommand.getConcedingTeamId()));
			}
			SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
			((EndGame) factory.forName(SequenceGenerator.Type.EndGame.name()))
				.pushSequence(new EndGame.SequenceParams(gameState, true));
			gameState.startNextStep();
		}
		return true;
	}

}
