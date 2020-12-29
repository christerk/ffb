package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.UtilNetCommand;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public abstract class InternalServerCommand extends NetCommand {

	protected static final String XML_ATTRIBUTE_GAME_ID = "gameId";

	private long fGameId;

	public InternalServerCommand() {
		this(0L);
	}

	public InternalServerCommand(long pGameId) {
		setGameId(pGameId);
	}

	public boolean isInternal() {
		return true;
	}

	public long getGameId() {
		return fGameId;
	}

	protected void setGameId(long pGameId) {
		fGameId = pGameId;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		if (fGameId > 0) {
			IJsonOption.GAME_ID.addTo(jsonObject, fGameId);
		}
		return jsonObject;
	}

	public InternalServerCommand initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		fGameId = 0L;
		if (IJsonOption.GAME_ID.isDefinedIn(jsonObject)) {
			fGameId = IJsonOption.GAME_ID.getFrom(game, jsonObject);
		}
		return this;
	}

}
