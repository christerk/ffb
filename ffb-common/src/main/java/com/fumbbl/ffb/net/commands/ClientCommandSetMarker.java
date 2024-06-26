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
public class ClientCommandSetMarker extends ClientCommand {

	private String fPlayerId;
	private FieldCoordinate fCoordinate;
	private String fText;

	public ClientCommandSetMarker() {
		super();
	}

	public ClientCommandSetMarker(FieldCoordinate pCoordinate, String pText) {
		fCoordinate = pCoordinate;
		fText = pText;
	}

	public ClientCommandSetMarker(String pPlayerId, String pText) {
		fPlayerId = pPlayerId;
		fText = pText;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SET_MARKER;
	}

	public FieldCoordinate getCoordinate() {
		return fCoordinate;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public String getText() {
		return fText;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.TEXT.addTo(jsonObject, fText);
		return jsonObject;
	}

	public ClientCommandSetMarker initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fCoordinate = IJsonOption.COORDINATE.getFrom(source, jsonObject);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fText = IJsonOption.TEXT.getFrom(source, jsonObject);
		return this;
	}

}
