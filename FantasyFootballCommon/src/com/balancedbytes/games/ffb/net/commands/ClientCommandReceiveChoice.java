package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandReceiveChoice extends ClientCommand {

	private boolean fChoiceReceive;

	public ClientCommandReceiveChoice() {
		super();
	}

	public ClientCommandReceiveChoice(boolean pChoiceReceive) {
		fChoiceReceive = pChoiceReceive;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_RECEIVE_CHOICE;
	}

	public boolean isChoiceReceive() {
		return fChoiceReceive;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CHOICE_RECEIVE.addTo(jsonObject, fChoiceReceive);
		return jsonObject;
	}

	public ClientCommandReceiveChoice initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fChoiceReceive = IJsonOption.CHOICE_RECEIVE.getFrom(game, jsonObject);
		return this;
	}

}
