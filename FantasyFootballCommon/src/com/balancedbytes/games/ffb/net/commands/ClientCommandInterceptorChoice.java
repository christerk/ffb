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
public class ClientCommandInterceptorChoice extends ClientCommand {

	private String fInterceptorId;

	public ClientCommandInterceptorChoice() {
		super();
	}

	public ClientCommandInterceptorChoice(String pInterceptorId) {
		fInterceptorId = pInterceptorId;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_INTERCEPTOR_CHOICE;
	}

	public String getInterceptorId() {
		return fInterceptorId;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.INTERCEPTOR_ID.addTo(jsonObject, fInterceptorId);
		return jsonObject;
	}

	public ClientCommandInterceptorChoice initFrom(Game game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fInterceptorId = IJsonOption.INTERCEPTOR_ID.getFrom(game, jsonObject);
		return this;
	}

}
