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
public class ServerCommandAdminMessage extends ServerCommand {

	private final List<String> fMessages;

	public ServerCommandAdminMessage() {
		fMessages = new ArrayList<>();
	}

	public ServerCommandAdminMessage(String[] pMessages) {
		this();
		addMessages(pMessages);
	}

	private void addMessage(String pMessage) {
		if (StringTool.isProvided(pMessage)) {
			fMessages.add(pMessage);
		}
	}

	private void addMessages(String[] pMessages) {
		if (ArrayTool.isProvided(pMessages)) {
			for (String message : pMessages) {
				addMessage(message);
			}
		}
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_ADMIN_MESSAGE;
	}

	public String[] getMessages() {
		return fMessages.toArray(new String[fMessages.size()]);
	}

	public boolean isReplayable() {
		return false;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.MESSAGE_ARRAY.addTo(jsonObject, fMessages);
		return jsonObject;
	}

	public ServerCommandAdminMessage initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		addMessages(IJsonOption.MESSAGE_ARRAY.getFrom(source, jsonObject));
		return this;
	}

}
