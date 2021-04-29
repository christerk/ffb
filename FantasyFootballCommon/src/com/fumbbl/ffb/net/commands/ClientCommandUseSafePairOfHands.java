package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUseSafePairOfHands extends ClientCommand {
	private boolean usingSafePairOfHands;

	public ClientCommandUseSafePairOfHands() {
	}

	public ClientCommandUseSafePairOfHands(boolean usingSafePairOfHands) {
		this.usingSafePairOfHands = usingSafePairOfHands;
	}

	public boolean isUsingSafePairOfHands() {
		return usingSafePairOfHands;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_SAFE_PAIR_OF_HANDS;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.USED.addTo(jsonObject, usingSafePairOfHands);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		usingSafePairOfHands = IJsonOption.USED.getFrom(game, UtilJson.toJsonObject(jsonValue));
		return this;
	}
}
