package com.fumbbl.ffb.server.net.commands;

import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.UtilNetCommand;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.util.ListTool;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandFumbblTeamLoaded extends InternalServerCommand {

	private String fCoach;
	private boolean fHomeTeam;
	private List<String> fAccountProperties;

	public InternalServerCommandFumbblTeamLoaded(long pGameId, String pCoach, boolean pHomeTeam, List<String> pAccountProperties) {
		super(pGameId);
		fCoach = pCoach;
		fHomeTeam = pHomeTeam;
		fAccountProperties = pAccountProperties;
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_FUMBBL_TEAM_LOADED;
	}

	public String getCoach() {
		return fCoach;
	}

	public boolean isHomeTeam() {
		return fHomeTeam;
	}

	public List<String> getAccountProperties() {
		return fAccountProperties;
	}

	
	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		IJsonOption.HOME_TEAM.addTo(jsonObject, fHomeTeam);
		IJsonOption.ACCOUNT_PROPERTIES.addTo(jsonObject,  fAccountProperties);
		return jsonObject;
	}

	public InternalServerCommandFumbblTeamLoaded initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		fCoach = IJsonOption.COACH.getFrom(game, jsonObject);
		fHomeTeam = IJsonOption.HOME_TEAM.getFrom(game, jsonObject);
		ListTool.replaceAll(fAccountProperties, IServerJsonOption.ACCOUNT_PROPERTIES.getFrom(game, jsonObject));

		return this;
	}
}
