package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRollSource;
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
	private ReRollSource reRollSource;

	public ClientCommandUseProReRollForBlock() {
		super();
	}

	public ClientCommandUseProReRollForBlock(int proIndex) {
		this(proIndex, null);
	}

	/**
	 * @param reRollSource Pro or a combination of Pro with fallback re-rolls, null defaults to Pro
	 */
	public ClientCommandUseProReRollForBlock(int proIndex, ReRollSource reRollSource) {
		this.proIndex = proIndex;
		this.reRollSource = reRollSource;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_PRO_RE_ROLL_FOR_BLOCK;
	}

	public int getProIndex() {
		return proIndex;
	}

	public ReRollSource getReRollSource() {
		return reRollSource;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PRO_INDEX.addTo(jsonObject, proIndex);
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, reRollSource);
		return jsonObject;
	}

	public ClientCommandUseProReRollForBlock initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		proIndex = IJsonOption.PRO_INDEX.getFrom(source, jsonObject);
		reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(source, jsonObject);
		return this;
	}

}
