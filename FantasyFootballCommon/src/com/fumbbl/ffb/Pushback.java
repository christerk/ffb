package com.fumbbl.ffb;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

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

	public Pushback initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fCoordinate = IJsonOption.COORDINATE.getFrom(source, jsonObject);
		return this;
	}

}
