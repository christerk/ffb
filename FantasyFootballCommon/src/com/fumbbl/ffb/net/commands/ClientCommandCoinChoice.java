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
public class ClientCommandCoinChoice extends ClientCommand {

	private boolean fChoiceHeads;

	public ClientCommandCoinChoice() {
		super();
	}

	public ClientCommandCoinChoice(boolean pChoiceHeads) {
		fChoiceHeads = pChoiceHeads;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_COIN_CHOICE;
	}

	public boolean isChoiceHeads() {
		return fChoiceHeads;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CHOICE_HEADS.addTo(jsonObject, fChoiceHeads);
		return jsonObject;
	}

	public ClientCommandCoinChoice initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fChoiceHeads = IJsonOption.CHOICE_HEADS.getFrom(game, jsonObject);
		return this;
	}

}
