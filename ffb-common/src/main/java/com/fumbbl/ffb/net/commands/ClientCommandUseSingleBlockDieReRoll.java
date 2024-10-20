package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * @author Kalimar
 */
public class ClientCommandUseSingleBlockDieReRoll extends ClientCommand {

	private int dieIndex;

	public ClientCommandUseSingleBlockDieReRoll() {
		super();
	}

	public ClientCommandUseSingleBlockDieReRoll(int dieIndex) {
		this.dieIndex = dieIndex;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_SINGLE_BLOCK_DIE_RE_ROLL;
	}

	public int getDieIndex() {
		return dieIndex;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.BLOCK_DIE_INDEX.addTo(jsonObject, dieIndex);
		return jsonObject;
	}

	public ClientCommandUseSingleBlockDieReRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		dieIndex = IJsonOption.BLOCK_DIE_INDEX.getFrom(source, jsonObject);
		return this;
	}

}
