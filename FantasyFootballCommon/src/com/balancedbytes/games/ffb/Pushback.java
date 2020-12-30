package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class Pushback implements IJsonSerializable {

	private String fPlayerId;
	private FieldCoordinate fCoordinate;

	public Pushback() {
		super();
	}

	public Pushback(String pPlayerId, FieldCoordinate pCoordinate) {
		fPlayerId = pPlayerId;
		fCoordinate = pCoordinate;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public FieldCoordinate getCoordinate() {
		return fCoordinate;
	}

	// Transformation

	public Pushback transform() {
		return new Pushback(getPlayerId(), FieldCoordinate.transform(getCoordinate()));
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
		return jsonObject;
	}

	public Pushback initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fCoordinate = IJsonOption.COORDINATE.getFrom(game, jsonObject);
		return this;
	}

}
