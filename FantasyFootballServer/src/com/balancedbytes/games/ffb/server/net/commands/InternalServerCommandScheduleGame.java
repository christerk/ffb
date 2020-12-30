package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.UtilNetCommand;
import com.balancedbytes.games.ffb.server.IGameIdListener;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

	public InternalServerCommandScheduleGame initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		fTeamHomeId = IJsonOption.TEAM_HOME_ID.getFrom(game, jsonObject);
		fTeamAwayId = IJsonOption.TEAM_AWAY_ID.getFrom(game, jsonObject);
		return this;
	}

}
