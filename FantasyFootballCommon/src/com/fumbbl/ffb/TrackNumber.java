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
public class TrackNumber implements IJsonSerializable {

	private FieldCoordinate fCoordinate;
	private int fNumber;

	public TrackNumber() {
		super();
	}

	public TrackNumber(FieldCoordinate pCoordinate, int pNumber) {
		fCoordinate = pCoordinate;
		fNumber = pNumber;
	}

	public FieldCoordinate getCoordinate() {
		return fCoordinate;
	}

	public int getNumber() {
		return fNumber;
	}

	public int hashCode() {
		return getCoordinate().hashCode();
	}

	public boolean equals(Object pObj) {
		return ((pObj instanceof TrackNumber) && getCoordinate().equals(((TrackNumber) pObj).getCoordinate()));
	}

	public TrackNumber transform() {
		return new TrackNumber(getCoordinate().transform(), getNumber());
	}

	public static TrackNumber transform(TrackNumber pTrackNumber) {
		return (pTrackNumber != null) ? pTrackNumber.transform() : null;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NUMBER.addTo(jsonObject, fNumber);
		IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
		return jsonObject;
	}

	public TrackNumber initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fNumber = IJsonOption.NUMBER.getFrom(game, jsonObject);
		fCoordinate = IJsonOption.COORDINATE.getFrom(game, jsonObject);
		return this;
	}

}
