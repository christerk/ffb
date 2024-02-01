package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.TeamList;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandTeamList extends ServerCommand {

	private TeamList fTeamList;

	public ServerCommandTeamList() {
		super();
	}

	public ServerCommandTeamList(TeamList pTeamList) {
		this();
		fTeamList = pTeamList;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_TEAM_LIST;
	}

	public TeamList getTeamList() {
		return fTeamList;
	}

	public boolean isReplayable() {
		return false;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		if (fTeamList != null) {
			IJsonOption.TEAM_LIST.addTo(jsonObject, fTeamList.toJsonValue());
		}
		return jsonObject;
	}

	public ServerCommandTeamList initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		JsonObject teamListObject = IJsonOption.TEAM_LIST.getFrom(source, jsonObject);
		if (teamListObject != null) {
			fTeamList = new TeamList().initFrom(source, teamListObject);
		} else {
			fTeamList = null;
		}
		return this;
	}

}
