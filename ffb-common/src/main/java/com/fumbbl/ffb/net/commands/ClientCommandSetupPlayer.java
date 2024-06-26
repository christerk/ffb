package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandSetupPlayer extends ClientCommand {

	private String fPlayerId;
	private FieldCoordinate fCoordinate;

	public ClientCommandSetupPlayer() {
		super();
	}

	public ClientCommandSetupPlayer(String pPlayerId, FieldCoordinate pCoordinate) {
		fPlayerId = pPlayerId;
		fCoordinate = pCoordinate;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SETUP_PLAYER;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public FieldCoordinate getCoordinate() {
		return fCoordinate;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
		return jsonObject;
	}

	public ClientCommandSetupPlayer initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fCoordinate = IJsonOption.COORDINATE.getFrom(source, jsonObject);
		return this;
	}

}
