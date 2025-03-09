package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandCalculateAutomaticPlayerMarkings;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import org.eclipse.jetty.websocket.api.Session;

public class FumbblRequestLoadPlayerMarkingsForGameVersion extends AbstractFumbblRequestLoadPlayerMarkings {

	private final Game game;
	private final int index;

	public FumbblRequestLoadPlayerMarkingsForGameVersion(Game game, int index, Session session) {
		super(session);
		this.game = game;
		this.index = index;
	}

	@Override
	public void process(ServerRequestProcessor processor) {
		FantasyFootballServer server = processor.getServer();

		AutoMarkingConfig config = server.getSessionManager().getAutoMarking(session);

		if (config == null) {
			config = loadAutomarkingConfig(server, game.getId(), game.getRules());
		}

		server.getCommunication().handleCommand(
			new ReceivedCommand(
				new InternalServerCommandCalculateAutomaticPlayerMarkings(config, index, game), session));
	}

}
