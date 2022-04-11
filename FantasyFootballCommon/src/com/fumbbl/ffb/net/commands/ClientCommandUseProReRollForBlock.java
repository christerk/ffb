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
public class ClientCommandUseProReRollForBlock extends ClientCommand {

	private int proIndex;

	public ClientCommandUseProReRollForBlock() {
		super();
	}

	public ClientCommandUseProReRollForBlock(int proIndex) {
		this.proIndex = proIndex;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_PRO_RE_ROLL_FOR_BLOCK;
	}

	public int getProIndex() {
		return proIndex;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PRO_INDEX.addTo(jsonObject, proIndex);
		return jsonObject;
	}

	public ClientCommandUseProReRollForBlock initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		proIndex = IJsonOption.PRO_INDEX.getFrom(source, jsonObject);
		return this;
	}

}
