package com.fumbbl.ffb.server.request.fumbbl;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.model.GameRules;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.ServerUrlProperty;
import com.fumbbl.ffb.server.marking.AutoMarkingConfig;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

public abstract class AbstractFumbblRequestLoadPlayerMarkings extends ServerRequest {

	protected final Session session;

	public AbstractFumbblRequestLoadPlayerMarkings(Session session) {
		this.session = session;
	}

	protected AutoMarkingConfig loadAutomarkingConfig(FantasyFootballServer server, String coach, long id, GameRules rules) {

		AutoMarkingConfig config = new AutoMarkingConfig();
		setRequestUrl(StringTool.bind(ServerUrlProperty.FUMBBL_PLAYER_MARKINGS.url(server.getProperties()),
			coach));

		try {
			String response = UtilServerHttpClient.fetchPage(getRequestUrl());
			@SuppressWarnings("deprecation") JsonValue jsonValue = JsonValue.readFrom(response);
			if (jsonValue != null && !jsonValue.isNull()) {
				config.initFrom(rules, jsonValue);
			}
		} catch (Throwable e) {
			server.getDebugLog().log(id, e);
		}
		return config;
	}

}
