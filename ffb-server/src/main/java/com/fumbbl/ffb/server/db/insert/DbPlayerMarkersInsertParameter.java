package com.fumbbl.ffb.server.db.insert;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.DefaultDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbPlayerMarkersInsertParameter extends DefaultDbUpdateParameter {

	private String fTeamId;
	private String fPlayerId;
	private String fText;

	public DbPlayerMarkersInsertParameter(String pTeamId, String pPlayerId, String pText) {
		fTeamId = pTeamId;
		fPlayerId = pPlayerId;
		fText = pText;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public String getText() {
		return fText;
	}

	public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
		return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.PLAYER_MARKERS_INSERT);
	}

}
