package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUseMultiBlockDiceReRoll extends ClientCommand {

	private int[] diceIndexes;

	public ClientCommandUseMultiBlockDiceReRoll() {
		super();
	}

	public ClientCommandUseMultiBlockDiceReRoll(int[] diceIndexes) {
		this.diceIndexes = diceIndexes;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_MULTI_BLOCK_DICE_RE_ROLL;
	}

	public int[] getDiceIndexes() {
		return diceIndexes;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.BLOCK_DICE_INDEXES.addTo(jsonObject, diceIndexes);
		return jsonObject;
	}

	public ClientCommandUseMultiBlockDiceReRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		diceIndexes = IJsonOption.BLOCK_DICE_INDEXES.getFrom(source, jsonObject);
		return this;
	}

}
