package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandSketchSetLabel extends ClientSketchCommand {

	private String sketchId;
	private String label;

	public ClientCommandSketchSetLabel() {
		super();
	}

	public ClientCommandSketchSetLabel(String sketchId, String label) {
		this.sketchId = sketchId;
		this.label = label;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SKETCH_SET_LABEL;
	}

	public String getSketchId() {
		return sketchId;
	}

	public String getLabel() {
		return label;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ID.addTo(jsonObject, sketchId);
		IJsonOption.TEXT.addTo(jsonObject, label);
		return jsonObject;
	}

	public ClientCommandSketchSetLabel initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		sketchId = IJsonOption.ID.getFrom(source, jsonObject);
		label = IJsonOption.TEXT.getFrom(source, jsonObject);
		return this;
	}

}
