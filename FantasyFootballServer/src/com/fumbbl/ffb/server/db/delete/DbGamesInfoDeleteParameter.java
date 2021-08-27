package com.fumbbl.ffb.server.db.delete;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.DefaultDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbGamesInfoDeleteParameter extends DefaultDbUpdateParameter {

	private long fGameStateId;

	public DbGamesInfoDeleteParameter(long pGameStateId) {
		fGameStateId = pGameStateId;
	}

	public long getGameStateId() {
		return fGameStateId;
	}

	public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
		return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.GAMES_INFO_DELETE);
	}

}
