package com.fumbbl.ffb.server.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.UtilNetCommand;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandUploadGame extends InternalServerCommand {

	private String fConcedingTeamId;

	public InternalServerCommandUploadGame(long pGameId) {
		this(pGameId, null);
	}

	public InternalServerCommandUploadGame(long pGameId, String pConcedingTeamId) {
		super(pGameId);
		fConcedingTeamId = pConcedingTeamId;
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_UPLOAD_GAME;
	}

	public String getConcedingTeamId() {
		return fConcedingTeamId;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CONCEDING_TEAM_ID.addTo(jsonObject, fConcedingTeamId);
		return jsonObject;
	}

	public InternalServerCommandUploadGame initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		fConcedingTeamId = IJsonOption.CONCEDING_TEAM_ID.getFrom(game, jsonObject);
		return this;
	}

}
