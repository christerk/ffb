package com.fumbbl.ffb.server.db.insert;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.IDbUpdateWithGameState;
import com.fumbbl.ffb.server.db.update.DbGamesSerializedUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedInsertParameter extends DbGamesSerializedUpdateParameter
		implements IDbUpdateWithGameState {

	public DbGamesSerializedInsertParameter(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
		return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.GAMES_SERIALIZED_INSERT);
	}

}
