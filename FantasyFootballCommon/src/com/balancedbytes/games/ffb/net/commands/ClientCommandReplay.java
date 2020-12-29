package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandReplay extends ClientCommand {

	private long fGameId;
	private int fReplayToCommandNr;

	public ClientCommandReplay() {
		super();
	}

	public ClientCommandReplay(long pGameId, int pReplayToCommandNr) {
		fGameId = pGameId;
		fReplayToCommandNr = pReplayToCommandNr;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_REPLAY;
	}

	public long getGameId() {
		return fGameId;
	}

	public int getReplayToCommandNr() {
		return fReplayToCommandNr;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.GAME_ID.addTo(jsonObject, fGameId);
		IJsonOption.REPLAY_TO_COMMAND_NR.addTo(jsonObject, fReplayToCommandNr);
		return jsonObject;
	}

	public ClientCommandReplay initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGameId = IJsonOption.GAME_ID.getFrom(game, jsonObject);
		fReplayToCommandNr = IJsonOption.REPLAY_TO_COMMAND_NR.getFrom(game, jsonObject);
		return this;
	}

}
