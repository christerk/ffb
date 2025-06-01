package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandSketchAddCoordinate extends ClientCommand {

	private String id;
	private FieldCoordinate coordinate;

	public ClientCommandSketchAddCoordinate() {
		super();
	}

	public ClientCommandSketchAddCoordinate(String id, FieldCoordinate coordinate) {
		this.id = id;
		this.coordinate = coordinate;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SKETCH_ADD_COORDINATE;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ID.addTo(jsonObject, id);
		IJsonOption.COORDINATE.addTo(jsonObject, coordinate);
		return jsonObject;
	}

	public ClientCommandSketchAddCoordinate initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		id = IJsonOption.ID.getFrom(source, jsonObject);
		coordinate = IJsonOption.COORDINATE.getFrom(source, jsonObject);
		return this;
	}

}
