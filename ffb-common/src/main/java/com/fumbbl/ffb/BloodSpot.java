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
public class BloodSpot implements IJsonSerializable {

	private PlayerState fInjury;
	private FieldCoordinate fCoordinate;
	private transient String fIconProperty;

	public BloodSpot() {
		super();
	}

	public BloodSpot(FieldCoordinate pCoordinate, PlayerState pInjury) {
		fInjury = pInjury;
		fCoordinate = pCoordinate;
	}

	public PlayerState getInjury() {
		return fInjury;
	}

	public FieldCoordinate getCoordinate() {
		return fCoordinate;
	}

	public void setIconProperty(String pIconProperty) {
		fIconProperty = pIconProperty;
	}

	public String getIconProperty() {
		return fIconProperty;
	}

	public BloodSpot transform() {
		BloodSpot transformedBloodspot = new BloodSpot(getCoordinate().transform(), getInjury());
		transformedBloodspot.setIconProperty(getIconProperty());
		return transformedBloodspot;
	}

	public static BloodSpot transform(BloodSpot pBloodspot) {
		return (pBloodspot != null) ? pBloodspot.transform() : null;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.INJURY.addTo(jsonObject, fInjury);
		IJsonOption.COORDINATE.addTo(jsonObject, fCoordinate);
		return jsonObject;
	}

	public BloodSpot initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fInjury = IJsonOption.INJURY.getFrom(source, jsonObject);
		fCoordinate = IJsonOption.COORDINATE.getFrom(source, jsonObject);
		return this;
	}

}
