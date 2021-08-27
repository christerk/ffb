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
public class ClientCommandUseApothecary extends ClientCommand {

	private String fPlayerId;
	private boolean fApothecaryUsed;

	public ClientCommandUseApothecary() {
		super();
	}

	public ClientCommandUseApothecary(String pPlayerId, boolean pApothecaryUsed) {
		fPlayerId = pPlayerId;
		fApothecaryUsed = pApothecaryUsed;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_APOTHECARY;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isApothecaryUsed() {
		return fApothecaryUsed;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.APOTHECARY_USED.addTo(jsonObject, fApothecaryUsed);
		return jsonObject;
	}

	public ClientCommandUseApothecary initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fApothecaryUsed = IJsonOption.APOTHECARY_USED.getFrom(game, jsonObject);
		return this;
	}

}
