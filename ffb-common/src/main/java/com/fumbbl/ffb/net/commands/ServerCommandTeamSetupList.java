package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandTeamSetupList extends ServerCommand {

	private final List<String> fSetupNames;

	public ServerCommandTeamSetupList() {
		fSetupNames = new ArrayList<>();
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

	public ServerCommandTeamSetupList initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		addSetupNames(IJsonOption.SETUP_NAMES.getFrom(source, jsonObject));
		return this;
	}

}
