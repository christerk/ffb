package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.IDbStatementFactory;
import com.fumbbl.ffb.server.db.query.DbPlayerMarkersQuery;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestLoadPlayerMarkings;
import org.eclipse.jetty.websocket.api.Session;

public class MarkerLoadingService {

	public void loadMarker(GameState gameState, Session session, boolean homeTeam, boolean auto) {
		if (auto) {
			gameState.getServer().getRequestProcessor().add(new FumbblRequestLoadPlayerMarkings(gameState, session));
		} else {
			IDbStatementFactory statementFactory = gameState.getServer().getDbQueryFactory();

			DbPlayerMarkersQuery dbPlayerMarkersQuery = (DbPlayerMarkersQuery) statementFactory
				.getStatement(DbStatementId.PLAYER_MARKERS_QUERY);
			dbPlayerMarkersQuery.execute(gameState, homeTeam);
		}
	}
}
