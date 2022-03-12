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
public class ClientCommandUseConsummateReRollForBlock extends ClientCommand {

	private int proIndex;

	public ClientCommandUseConsummateReRollForBlock() {
		super();
	}

	public ClientCommandUseConsummateReRollForBlock(int proIndex) {
		this.proIndex = proIndex;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_CONSUMMATE_RE_ROLL_FOR_BLOCK;
	}

	public int getProIndex() {
		return proIndex;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PRO_INDEX.addTo(jsonObject, proIndex);
		return jsonObject;
	}

	public ClientCommandUseConsummateReRollForBlock initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		proIndex = IJsonOption.PRO_INDEX.getFrom(game, jsonObject);
		return this;
	}

}
