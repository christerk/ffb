package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandBloodlustAction extends ClientCommand {

	private boolean change;

	public ClientCommandBloodlustAction() {
	}

	public ClientCommandBloodlustAction(boolean change) {
		this.change = change;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_BLOODLUST_ACTION;
	}

	public boolean isChange() {
		return change;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CHANGE_TO_MOVE.addTo(jsonObject, change);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		change = IJsonOption.CHANGE_TO_MOVE.getFrom(source, jsonObject);
		return this;
	}

}
