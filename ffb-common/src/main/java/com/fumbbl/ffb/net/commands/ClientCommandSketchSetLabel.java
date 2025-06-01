package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandSketchSetLabel extends ClientCommand {

	private String id;
	private String label;

	public ClientCommandSketchSetLabel() {
		super();
	}

	public ClientCommandSketchSetLabel(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SKETCH_SET_LABEL;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ID.addTo(jsonObject, id);
		IJsonOption.TEXT.addTo(jsonObject, label);
		return jsonObject;
	}

	public ClientCommandSketchSetLabel initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		id = IJsonOption.ID.getFrom(source, jsonObject);
		label = IJsonOption.TEXT.getFrom(source, jsonObject);
		return this;
	}

}
