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
public class ClientCommandBlockChoice extends ClientCommand {

	private int fDiceIndex;

	public ClientCommandBlockChoice() {
		super();
	}

	public ClientCommandBlockChoice(int pDiceIndex) {
		fDiceIndex = pDiceIndex;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_BLOCK_CHOICE;
	}

	public int getDiceIndex() {
		return fDiceIndex;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.DICE_INDEX.addTo(jsonObject, fDiceIndex);
		return jsonObject;
	}

	public ClientCommandBlockChoice initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fDiceIndex = IJsonOption.DICE_INDEX.getFrom(source, jsonObject);
		return this;
	}

}
