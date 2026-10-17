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
public class ClientCommandApothecaryChoice extends ClientCommand {

	private boolean fChoiceNewInjury;

	public ClientCommandApothecaryChoice() {
		super();
	}

	public ClientCommandApothecaryChoice(boolean pChoiceNewInjury) {
		fChoiceNewInjury = pChoiceNewInjury;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_APOTHECARY_CHOICE;
	}

	public boolean isChoiceNewInjury() {
		return fChoiceNewInjury;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CHOICE_NEW_INJURY.addTo(jsonObject, fChoiceNewInjury);
		return jsonObject;
	}

	public ClientCommandApothecaryChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fChoiceNewInjury = IJsonOption.CHOICE_NEW_INJURY.getFrom(source, jsonObject);
		return this;
	}

}
