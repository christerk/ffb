package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.ArrayList;
import java.util.List;

public class ServerCommandUpdateLocalPlayerMarkers extends ServerCommand {

	private List<PlayerMarker> markers = new ArrayList<>();

	public ServerCommandUpdateLocalPlayerMarkers() {
	}

	public ServerCommandUpdateLocalPlayerMarkers(List<PlayerMarker> markers) {
		this.markers = markers;
	}

	public List<PlayerMarker> getMarkers() {
		return markers;
	}

	@Override
	public ServerCommandUpdateLocalPlayerMarkers initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		JsonArray playerMarkerArray = IJsonOption.PLAYER_MARKER_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < playerMarkerArray.size(); i++) {
			markers.add(new PlayerMarker().initFrom(source, playerMarkerArray.get(i)));
		}
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());

		JsonArray playerMarkerArray = new JsonArray();
		for (PlayerMarker playerMarker : markers) {
			playerMarkerArray.add(playerMarker.toJsonValue());
		}
		IJsonOption.PLAYER_MARKER_ARRAY.addTo(jsonObject, playerMarkerArray);
		return jsonObject;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_UPDATE_LOCAL_PLAYER_MARKERS;
	}
}
