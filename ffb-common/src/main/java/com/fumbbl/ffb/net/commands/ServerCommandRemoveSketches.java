package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.Arrays;
import java.util.List;

public class ServerCommandRemoveSketches extends ClientCommand {

	private String coach;
	private List<String> ids;

	public ServerCommandRemoveSketches() {
		super();
	}

	public ServerCommandRemoveSketches(String coach, List<String> ids) {
		this.coach = coach;
		this.ids = ids;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_REMOVE_SKETCHES;
	}

	public String getCoach() {
		return coach;
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
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ServerCommandRemoveSketches initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		if (IJsonOption.IDS.isDefinedIn(jsonObject)) {
			ids = Arrays.asList(IJsonOption.IDS.getFrom(source, jsonObject));
		}
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
