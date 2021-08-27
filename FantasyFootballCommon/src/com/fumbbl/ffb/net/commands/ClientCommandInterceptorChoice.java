package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

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

	public ClientCommandInterceptorChoice initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fInterceptorId = IJsonOption.INTERCEPTOR_ID.getFrom(game, jsonObject);
		return this;
	}

}
