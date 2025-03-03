package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandCalculateAutomaticPlayerMarkings;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import org.eclipse.jetty.websocket.api.Session;

import java.util.List;

public class FumbblRequestLoadMultiplePlayerMarkings extends AbstractFumbblRequestLoadPlayerMarkings {

	private final List<Game> games;

	public FumbblRequestLoadMultiplePlayerMarkings(List<Game> games, Session session) {
		super(session);
		this.games = games;
	}

	@Override
	public void process(ServerRequestProcessor processor) {

		if (!games.isEmpty()) {
			Game game = games.get(0);
			AutoMarkingConfig config = loadAutomarkingConfig(processor.getServer(), game.getId(), game.getRules());

			processor.getServer().getCommunication().handleCommand(
				new ReceivedCommand(
					new InternalServerCommandCalculateAutomaticPlayerMarkings(config, games), session));
		}
	}

}
