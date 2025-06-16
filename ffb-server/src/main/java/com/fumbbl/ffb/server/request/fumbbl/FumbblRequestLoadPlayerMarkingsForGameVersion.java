package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ReplaySessionManager;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandCalculateAutomaticPlayerMarkings;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import org.eclipse.jetty.websocket.api.Session;

public class FumbblRequestLoadPlayerMarkingsForGameVersion extends AbstractFumbblRequestLoadPlayerMarkings {

	private final Game game;
	private final int index;
	private final String coach;

	public FumbblRequestLoadPlayerMarkingsForGameVersion(Game game, int index, String coach, Session session) {
		super(session);
		this.game = game;
		this.index = index;
		this.coach = coach;
	}

	@Override
	public void process(ServerRequestProcessor processor) {
		FantasyFootballServer server = processor.getServer();

		ReplaySessionManager sessionManager = server.getReplaySessionManager();

		AutoMarkingConfig config = sessionManager.getAutoMarking(session);

		if (config == null) {
			config = loadAutomarkingConfig(server, coach, game.getId(), game.getRules());
			sessionManager.addAutoMarking(session, config);
		}

		server.getCommunication().handleCommand(
			new ReceivedCommand(
				new InternalServerCommandCalculateAutomaticPlayerMarkings(config, index, game), session));
	}

}
