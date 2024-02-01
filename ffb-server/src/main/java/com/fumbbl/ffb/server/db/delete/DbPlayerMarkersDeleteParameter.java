package com.fumbbl.ffb.server.db.delete;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;

/**
 * 
 * @author Kalimar
 */
public class DbPlayerMarkersDeleteParameter extends DefaultDbUpdateParameter {

	private String fTeamId;

	public DbPlayerMarkersDeleteParameter(String pTeamId) {
		fTeamId = pTeamId;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
		return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.PLAYER_MARKERS_DELETE);
	}

}
