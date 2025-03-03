package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandApplyAutomatedPlayerMarkings;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import org.eclipse.jetty.websocket.api.Session;

public class FumbblRequestLoadPlayerMarkings extends AbstractFumbblRequestLoadPlayerMarkings {

	private final GameState gameState;

	public FumbblRequestLoadPlayerMarkings(GameState gameState, Session session) {
		super(session);
		this.gameState = gameState;
	}

	@Override
	public void process(ServerRequestProcessor processor) {

		Game game = gameState.getGame();
		AutoMarkingConfig config = loadAutomarkingConfig(processor.getServer(), game.getId(), game.getRules());

		processor.getServer().getCommunication().handleCommand(
			new ReceivedCommand(
				new InternalServerCommandApplyAutomatedPlayerMarkings(config, gameState.getId()), session));
	}


}
