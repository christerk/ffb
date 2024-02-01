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
public class ClientCommandFollowupChoice extends ClientCommand {

	private boolean fChoiceFollowup;

	public ClientCommandFollowupChoice() {
		super();
	}

	public ClientCommandFollowupChoice(boolean pChoiceReceive) {
		fChoiceFollowup = pChoiceReceive;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_FOLLOWUP_CHOICE;
	}

	public boolean isChoiceFollowup() {
		return fChoiceFollowup;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CHOICE_FOLLOWUP.addTo(jsonObject, fChoiceFollowup);
		return jsonObject;
	}

	public ClientCommandFollowupChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fChoiceFollowup = IJsonOption.CHOICE_FOLLOWUP.getFrom(source, jsonObject);
		return this;
	}

}
