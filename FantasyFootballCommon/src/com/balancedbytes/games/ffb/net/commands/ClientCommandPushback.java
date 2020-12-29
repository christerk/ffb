package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.Pushback;
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
public class ClientCommandPushback extends ClientCommand {

	private Pushback fPushback;

	public ClientCommandPushback() {
		super();
	}

	public ClientCommandPushback(Pushback pPushback) {
		if (pPushback == null) {
			throw new IllegalArgumentException("Parameter pushback must not be null.");
		}
		fPushback = pPushback;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_PUSHBACK;
	}

	public Pushback getPushback() {
		return fPushback;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PUSHBACK.addTo(jsonObject, fPushback.toJsonValue());
		return jsonObject;
	}

	public ClientCommandPushback initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPushback = new Pushback();
		fPushback.initFrom(game, IJsonOption.PUSHBACK.getFrom(game, jsonObject));
		return this;
	}

}
