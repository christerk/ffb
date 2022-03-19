package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

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

	public ClientCommandPushback initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPushback = new Pushback();
		fPushback.initFrom(source, IJsonOption.PUSHBACK.getFrom(source, jsonObject));
		return this;
	}

}
