package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.GameList;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

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

	public ServerCommandGameList initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		JsonObject gameListObject = IJsonOption.GAME_LIST.getFrom(source, jsonObject);
		if (gameListObject != null) {
			fGameList = new GameList().initFrom(source, gameListObject);
		} else {
			fGameList = null;
		}
		return this;
	}

}
