package com.fumbbl.ffb.server.db.update;

import java.io.IOException;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.DefaultDbUpdateParameter;
import com.fumbbl.ffb.server.db.IDbUpdateWithGameState;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedUpdateParameter extends DefaultDbUpdateParameter implements IDbUpdateWithGameState {

	private long fId;
	private JsonObject fJsonObject;
	private GameState fGameState;

	public DbGamesSerializedUpdateParameter(GameState pGameState) {
		fGameState = pGameState;
		if (fGameState != null) {
			fId = fGameState.getId();
			fJsonObject = fGameState.toJsonValue();
		}
	}

	public long getId() {
		return fId;
	}

	public int length() {
		return fJsonObject.toString().length();
	}

	public GameState getGameState() {
		return fGameState;
	}

	public byte[] gzip() throws IOException {
		return UtilJson.gzip(fJsonObject);
	}

	public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
		return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.GAMES_SERIALIZED_UPDATE);
	}

}
