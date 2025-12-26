package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandPickUpChoice extends ClientCommand {

	private boolean attemptPickUp;

	public ClientCommandPickUpChoice() {
		super();
	}

	public ClientCommandPickUpChoice(boolean pChoiceReceive) {
		attemptPickUp = pChoiceReceive;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_PICK_UP_CHOICE;
	}

	public boolean isChoicePickUp() {
		return attemptPickUp;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CHOICE_PICK_UP.addTo(jsonObject, attemptPickUp);
		return jsonObject;
	}

	public ClientCommandPickUpChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		attemptPickUp = IJsonOption.CHOICE_PICK_UP.getFrom(source, jsonObject);
		return this;
	}

}
