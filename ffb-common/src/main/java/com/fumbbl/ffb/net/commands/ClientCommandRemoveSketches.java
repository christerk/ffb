package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.Arrays;
import java.util.List;

public class ClientCommandRemoveSketches extends ClientSketchCommand {

	private List<String> ids;

	public ClientCommandRemoveSketches() {
		super();
	}

	public ClientCommandRemoveSketches(List<String> ids) {
		this.ids = ids;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_REMOVE_SKETCHES;
	}

	public List<String> getIds() {
		return ids;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (ids != null && !ids.isEmpty()) {
			IJsonOption.IDS.addTo(jsonObject, ids);
		}
		return jsonObject;
	}

	public ClientCommandRemoveSketches initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		if (IJsonOption.IDS.isDefinedIn(jsonObject)) {
			ids = Arrays.asList(IJsonOption.IDS.getFrom(source, jsonObject));
		}
		return this;
	}

}
