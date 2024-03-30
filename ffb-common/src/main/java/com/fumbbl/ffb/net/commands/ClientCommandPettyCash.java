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
public class ClientCommandPettyCash extends ClientCommand {

	private int fPettyCash;

	public ClientCommandPettyCash() {
		super();
	}

	public ClientCommandPettyCash(int pPettyCash) {
		fPettyCash = pPettyCash;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_PETTY_CASH;
	}

	public int getPettyCash() {
		return fPettyCash;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PETTY_CASH.addTo(jsonObject, fPettyCash);
		return jsonObject;
	}

	public ClientCommandPettyCash initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPettyCash = IJsonOption.PETTY_CASH.getFrom(source, jsonObject);
		return this;
	}

}
