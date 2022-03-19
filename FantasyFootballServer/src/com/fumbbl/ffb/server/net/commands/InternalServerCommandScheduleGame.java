package com.fumbbl.ffb.server.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.UtilNetCommand;
import com.fumbbl.ffb.server.IGameIdListener;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandScheduleGame extends InternalServerCommand {

	private String fTeamHomeId;
	private String fTeamAwayId;

	private transient IGameIdListener fGameIdListener;

	public InternalServerCommandScheduleGame(String pTeamHomeId, String pTeamAwayId) {
		fTeamHomeId = pTeamHomeId;
		fTeamAwayId = pTeamAwayId;
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_SCHEDULE_GAME;
	}

	public String getTeamHomeId() {
		return fTeamHomeId;
	}

	public String getTeamAwayId() {
		return fTeamAwayId;
	}

	public void setGameIdListener(IGameIdListener pGameIdListener) {
		fGameIdListener = pGameIdListener;
	}

	public IGameIdListener getGameIdListener() {
		return fGameIdListener;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.TEAM_HOME_ID.addTo(jsonObject, fTeamHomeId);
		IJsonOption.TEAM_AWAY_ID.addTo(jsonObject, fTeamAwayId);
		return jsonObject;
	}

	public InternalServerCommandScheduleGame initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		fTeamHomeId = IJsonOption.TEAM_HOME_ID.getFrom(source, jsonObject);
		fTeamAwayId = IJsonOption.TEAM_AWAY_ID.getFrom(source, jsonObject);
		return this;
	}

}
