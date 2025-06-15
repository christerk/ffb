package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.Arrays;
import java.util.List;

public class ClientCommandSketchSetLabel extends ClientSketchCommand {

	private List<String> sketchIds;
	private String label;

	public ClientCommandSketchSetLabel() {
		super();
	}

	public ClientCommandSketchSetLabel(List<String> sketchIds, String label) {
		this.sketchIds = sketchIds;
		this.label = label;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SKETCH_SET_LABEL;
	}

	public List<String> getSketchIds() {
		return sketchIds;
	}

	public String getLabel() {
		return label;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.IDS.addTo(jsonObject, sketchIds);
		IJsonOption.TEXT.addTo(jsonObject, label);
		return jsonObject;
	}

	public ClientCommandSketchSetLabel initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		sketchIds = Arrays.asList(IJsonOption.IDS.getFrom(source, jsonObject));
		label = IJsonOption.TEXT.getFrom(source, jsonObject);
		return this;
	}

}
