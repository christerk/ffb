package com.fumbbl.ffb.server.request.fumbbl;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.CommonPropertyValue;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.query.DbUserSettingsQuery;
import com.fumbbl.ffb.server.marking.AutoMarkingConfig;
import com.fumbbl.ffb.model.GameRules;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.ServerUrlProperty;
import com.fumbbl.ffb.server.marking.SortMode;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;

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

	protected void updateSearchMode(FantasyFootballServer server, String coach, long id, AutoMarkingConfig config) {
		try {
			Map<CommonProperty, String> settings = new HashMap<>();
			DbUserSettingsQuery settingsQuery = (DbUserSettingsQuery) server.getDbQueryFactory().getStatement(DbStatementId.USER_SETTINGS_QUERY);
			settingsQuery.execute(coach, settings);
			String sortMode = settings.get(CommonProperty.SETTING_PLAYER_MARKING_TYPE);
			if (CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO.equals(sortMode)) {
				config.setSortMode(SortMode.DEFAULT);
			} else if (CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO_NO_SORT.equals(sortMode)) {
				config.setSortMode(SortMode.NONE);
			}
		} catch (Throwable e) {
			server.getDebugLog().log(id, e);
		}
	}
}
