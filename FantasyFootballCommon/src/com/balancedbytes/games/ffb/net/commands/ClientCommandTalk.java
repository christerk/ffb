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
public class ClientCommandTalk extends ClientCommand {

	private String fTalk;

	public ClientCommandTalk() {
		super();
	}

	public ClientCommandTalk(String pTalk) {
		fTalk = pTalk;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_TALK;
	}

	public String getTalk() {
		return fTalk;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.TALK.addTo(jsonObject, fTalk);
		return jsonObject;
	}

	public ClientCommandTalk initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fTalk = IJsonOption.TALK.getFrom(game, jsonObject);
		return this;
	}

}
