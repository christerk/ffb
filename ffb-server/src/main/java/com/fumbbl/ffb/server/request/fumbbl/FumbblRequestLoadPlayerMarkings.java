package com.fumbbl.ffb.server.request.fumbbl;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ServerUrlProperty;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandApplyAutomatedPlayerMarkings;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

public class FumbblRequestLoadPlayerMarkings extends ServerRequest {


	private final GameState gameState;
	private final Session session;

	public FumbblRequestLoadPlayerMarkings(GameState gameState, Session session) {
		this.gameState = gameState;
		this.session = session;
	}

	@Override
	public void process(ServerRequestProcessor processor) {
		SessionManager sessionManager = processor.getServer().getSessionManager();

		Game game = gameState.getGame();
		AutoMarkingConfig config = new AutoMarkingConfig();

		String coach = sessionManager.getCoachForSession(session);
		setRequestUrl(StringTool.bind(ServerUrlProperty.FUMBBL_PLAYER_MARKINGS.url(processor.getServer().getProperties()),
			coach));

		try {
			String response = UtilServerHttpClient.fetchPage(getRequestUrl());
			JsonValue jsonValue = JsonValue.readFrom(response);
			if (jsonValue != null && !jsonValue.isNull()) {
				config.initFrom(game.getRules(), jsonValue);
			}
		} catch (Throwable e) {
			processor.getServer().getDebugLog().log(game.getId(), e);
		}

		processor.getServer().getCommunication().handleCommand(
			new ReceivedCommand(
				new InternalServerCommandApplyAutomatedPlayerMarkings(config, gameState.getId()), session));
	}
}
