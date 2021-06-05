package com.fumbbl.ffb.model.stadium;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

public class TrapDoor implements OnPitchEnhancement, IJsonSerializable {
	private FieldCoordinate coordinate;

	public TrapDoor() {
	}

	public TrapDoor(FieldCoordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public FieldCoordinate getCoordinate() {
		return coordinate;
	}

	@Override
	public String getIconProperty() {
		return IIconProperty.GAME_TRAP_DOOR;
	}

	@Override
	public TrapDoor initFrom(IFactorySource game, JsonValue pJsonValue) {
		coordinate = IJsonOption.COORDINATE.getFrom(game, UtilJson.toJsonObject(pJsonValue));
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.COORDINATE.addTo(jsonObject, coordinate);
		return jsonObject;
	}
}
