package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandTalk extends ClientCommand {

	private String fTalk;

	public ClientCommandTalk() {
		super();
	}

	public ClientCommandTalk(String pTalk) {
		fTalk = pTalk;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_TALK;
	}

	public String getTalk() {
		return fTalk;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.TALK.addTo(jsonObject, fTalk);
		return jsonObject;
	}

	public ClientCommandTalk initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fTalk = IJsonOption.TALK.getFrom(source, jsonObject);
		return this;
	}

}
