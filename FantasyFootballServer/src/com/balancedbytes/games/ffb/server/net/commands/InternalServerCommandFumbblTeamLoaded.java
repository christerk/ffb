package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.UtilNetCommand;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandFumbblTeamLoaded extends InternalServerCommand {

	private String fCoach;
	private boolean fHomeTeam;

	public InternalServerCommandFumbblTeamLoaded(long pGameId, String pCoach, boolean pHomeTeam) {
		super(pGameId);
		fCoach = pCoach;
		fHomeTeam = pHomeTeam;
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

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		IJsonOption.HOME_TEAM.addTo(jsonObject, fHomeTeam);
		return jsonObject;
	}

	public InternalServerCommandFumbblTeamLoaded initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		fCoach = IJsonOption.COACH.getFrom(game, jsonObject);
		fHomeTeam = IJsonOption.HOME_TEAM.getFrom(game, jsonObject);
		return this;
	}

}
