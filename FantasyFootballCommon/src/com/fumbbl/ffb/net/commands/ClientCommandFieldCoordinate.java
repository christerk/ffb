package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandFieldCoordinate extends ClientCommand {

	private FieldCoordinate fieldCoordinate;

	public ClientCommandFieldCoordinate() {
		super();
	}

	public ClientCommandFieldCoordinate(FieldCoordinate fieldCoordinate) {
		if (fieldCoordinate == null) {
			throw new IllegalArgumentException("Parameter fieldCoordinate must not be null.");
		}
		this.fieldCoordinate = fieldCoordinate;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_FIELD_COORDINATE;
	}

	public FieldCoordinate getFieldCoordinate() {
		return fieldCoordinate;
	}

// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.FIELD_COORDINATE.addTo(jsonObject, fieldCoordinate.toJsonValue());
		return jsonObject;
	}

	public ClientCommandFieldCoordinate initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fieldCoordinate = new FieldCoordinate(0);
		fieldCoordinate.initFrom(game, IJsonOption.FIELD_COORDINATE.getFrom(game, jsonObject));
		return this;
	}

}
