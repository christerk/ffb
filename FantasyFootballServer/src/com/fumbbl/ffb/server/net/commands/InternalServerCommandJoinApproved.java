package com.fumbbl.ffb.server.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.UtilNetCommand;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandJoinApproved extends InternalServerCommand {

	private String fCoach;
	private String fGameName;
	private ClientMode fClientMode;
	private String fTeamId;

	public InternalServerCommandJoinApproved(long pGameId, String pGameName, String pCoach, String pTeamId,
			ClientMode pClientMode) {
		super(pGameId);
		fGameName = pGameName;
		fCoach = pCoach;
		fTeamId = pTeamId;
		fClientMode = pClientMode;
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_JOIN_APPROVED;
	}

	public String getCoach() {
		return fCoach;
	}

	public String getGameName() {
		return fGameName;
	}

	public ClientMode getClientMode() {
		return fClientMode;
	}

	public String getTeamId() {
		return fTeamId;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		IJsonOption.GAME_NAME.addTo(jsonObject, fGameName);
		IJsonOption.CLIENT_MODE.addTo(jsonObject, fClientMode);
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		return jsonObject;
	}

	public InternalServerCommandJoinApproved initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		fCoach = IJsonOption.COACH.getFrom(game, jsonObject);
		fGameName = IJsonOption.GAME_NAME.getFrom(game, jsonObject);
		fClientMode = (ClientMode) IJsonOption.CLIENT_MODE.getFrom(game, jsonObject);
		fTeamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		return this;
	}

}
