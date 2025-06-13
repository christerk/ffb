package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
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
		SessionManager sessionManager = processor.getServer().getSessionManager();
		String coach = sessionManager.getCoachForSession(session);
		Game game = gameState.getGame();

		AutoMarkingConfig config = loadAutomarkingConfig(processor.getServer(), coach, game.getId(), game.getRules());
		sessionManager.addAutoMarking(session, config);

		processor.getServer().getCommunication().handleCommand(
			new ReceivedCommand(
				new InternalServerCommandApplyAutomatedPlayerMarkings(config, gameState.getId()), session));
	}
}
