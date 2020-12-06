package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

	public ClientCommandPettyCash initFrom(JsonValue jsonValue) {
		super.initFrom(jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPettyCash = IJsonOption.PETTY_CASH.getFrom(jsonObject);
		return this;
	}

}
