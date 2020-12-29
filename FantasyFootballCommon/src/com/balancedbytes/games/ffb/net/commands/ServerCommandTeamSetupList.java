package com.balancedbytes.games.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandTeamSetupList extends ServerCommand {

	private List<String> fSetupNames;

	public ServerCommandTeamSetupList() {
		fSetupNames = new ArrayList<String>();
	}

	public ServerCommandTeamSetupList(String[] pSetupNames) {
		this();
		addSetupNames(pSetupNames);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_TEAM_SETUP_LIST;
	}

	public String[] getSetupNames() {
		return fSetupNames.toArray(new String[fSetupNames.size()]);
	}

	private void addSetupName(String pSetupName) {
		if (StringTool.isProvided(pSetupName)) {
			fSetupNames.add(pSetupName);
		}
	}

	private void addSetupNames(String[] pSetupNames) {
		if (ArrayTool.isProvided(pSetupNames)) {
			for (String setupName : pSetupNames) {
				addSetupName(setupName);
			}
		}
	}

	public boolean isReplayable() {
		return false;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.SETUP_NAMES.addTo(jsonObject, fSetupNames);
		return jsonObject;
	}

	public ServerCommandTeamSetupList initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(game, jsonObject));
		addSetupNames(IJsonOption.SETUP_NAMES.getFrom(game, jsonObject));
		return this;
	}

}
