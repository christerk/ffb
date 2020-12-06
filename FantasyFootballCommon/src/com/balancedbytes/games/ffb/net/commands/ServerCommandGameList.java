package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.GameList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandGameList extends ServerCommand {

	private GameList fGameList;

	public ServerCommandGameList() {
		super();
	}

	public ServerCommandGameList(GameList pGameList) {
		this();
		fGameList = pGameList;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_GAME_LIST;
	}

	public GameList getGameList() {
		return fGameList;
	}

	public boolean isReplayable() {
		return false;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		if (fGameList != null) {
			IJsonOption.GAME_LIST.addTo(jsonObject, fGameList.toJsonValue());
		}
		return jsonObject;
	}

	public ServerCommandGameList initFrom(JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(jsonObject));
		JsonObject gameListObject = IJsonOption.GAME_LIST.getFrom(jsonObject);
		if (gameListObject != null) {
			fGameList = new GameList().initFrom(gameListObject);
		} else {
			fGameList = null;
		}
		return this;
	}

}
