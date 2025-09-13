package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.marking.SortMode;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUpdatePlayerMarkings extends ClientCommand {

	private boolean auto;
	private SortMode sortMode;

	public ClientCommandUpdatePlayerMarkings() {
	}

	public ClientCommandUpdatePlayerMarkings(boolean auto, SortMode sortMode) {
		this.auto = auto;
		this.sortMode = sortMode;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_UPDATE_PLAYER_MARKINGS;
	}

	public SortMode getSortMode() {
		return sortMode;
	}

	public boolean isAuto() {
		return auto;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.USE_AUTO_MARKINGS.addTo(jsonObject, auto);
		if (sortMode != null) {
			IJsonOption.SORT_MODE.addTo(jsonObject, sortMode.name());
		}
		return jsonObject;
	}

	public ClientCommandUpdatePlayerMarkings initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		auto = IJsonOption.USE_AUTO_MARKINGS.getFrom(source, jsonObject);
		if (IJsonOption.SORT_MODE.isDefinedIn(jsonObject)) {
				sortMode = SortMode.valueOf(IJsonOption.SORT_MODE.getFrom(source, jsonObject));
		}
		return this;
	}
}
