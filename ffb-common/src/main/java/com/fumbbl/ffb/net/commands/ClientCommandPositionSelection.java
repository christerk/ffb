package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandPositionSelection extends ClientCommand {

	private String[] position;
	private String teamId;

	public ClientCommandPositionSelection() {
	}

	public ClientCommandPositionSelection(String[] position, String teamId) {
		this.position = position;
		this.teamId = teamId;
	}

	public String[] getPosition() {
		return position;
	}

	public String getTeamId() {
		return teamId;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_POSITION_SELECTION;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.POSITION_IDS.addTo(jsonObject, position);
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		position = IJsonOption.POSITION_IDS.getFrom(source, jsonObject);
		teamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		return this;
	}
}
