package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.CommonPropertyValue;
import com.fumbbl.ffb.marking.SortMode;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.query.DbUserSettingsQuery;
import com.fumbbl.ffb.server.marking.AutoMarkingConfig;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ReplaySessionManager;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandCalculateAutomaticPlayerMarkings;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;

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
		updateSearchMode(processor.getServer(), coach, game.getId(), config);

		server.getCommunication().handleCommand(
			new ReceivedCommand(
				new InternalServerCommandCalculateAutomaticPlayerMarkings(config, index, game), session));
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
