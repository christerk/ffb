package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandAddSketch extends ClientCommand {

	private Sketch sketch;

	public ClientCommandAddSketch() {
		super();
	}

	public ClientCommandAddSketch(Sketch sketch) {
		this.sketch = sketch;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_ADD_SKETCH;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.SKETCH.addTo(jsonObject, sketch.toJsonValue());
		return jsonObject;
	}

	public ClientCommandAddSketch initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		sketch = new Sketch(0).initFrom(source, IJsonOption.SKETCH.getFrom(source, jsonObject));
		return this;
	}

}
