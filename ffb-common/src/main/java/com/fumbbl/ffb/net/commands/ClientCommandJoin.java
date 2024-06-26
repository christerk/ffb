package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandJoin extends ClientCommand {

	private String fCoach;
	private String fPassword;
	private long fGameId;
	private String fGameName;
	private ClientMode fClientMode;
	private String fTeamId;
	private String fTeamName;

	public ClientCommandJoin() {
		super();
	}

	public ClientCommandJoin(ClientMode pClientMode) {
		fClientMode = pClientMode;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_JOIN;
	}

	public ClientMode getClientMode() {
		return fClientMode;
	}

	public void setClientMode(ClientMode pClientMode) {
		fClientMode = pClientMode;
	}

	public String getCoach() {
		return fCoach;
	}

	public void setCoach(String pCoach) {
		fCoach = pCoach;
	}

	public String getPassword() {
		return fPassword;
	}

	public void setPassword(String pPassword) {
		fPassword = pPassword;
	}

	public long getGameId() {
		return fGameId;
	}

	public void setGameId(long pGameId) {
		fGameId = pGameId;
	}

	public String getGameName() {
		return fGameName;
	}

	public void setGameName(String pGameName) {
		fGameName = pGameName;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public void setTeamId(String pTeamId) {
		fTeamId = pTeamId;
	}

	public String getTeamName() {
		return fTeamName;
	}

	public void setTeamName(String pTeamName) {
		fTeamName = pTeamName;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CLIENT_MODE.addTo(jsonObject, fClientMode);
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		IJsonOption.PASSWORD.addTo(jsonObject, fPassword);
		IJsonOption.GAME_ID.addTo(jsonObject, fGameId);
		IJsonOption.GAME_NAME.addTo(jsonObject, fGameName);
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		IJsonOption.TEAM_NAME.addTo(jsonObject, fTeamName);
		return jsonObject;
	}

	public ClientCommandJoin initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fClientMode = (ClientMode) IJsonOption.CLIENT_MODE.getFrom(source, jsonObject);
		fCoach = IJsonOption.COACH.getFrom(source, jsonObject);
		fPassword = IJsonOption.PASSWORD.getFrom(source, jsonObject);
		fGameId = IJsonOption.GAME_ID.getFrom(source, jsonObject);
		fGameName = IJsonOption.GAME_NAME.getFrom(source, jsonObject);
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fTeamName = IJsonOption.TEAM_NAME.getFrom(source, jsonObject);
		return this;
	}

}
