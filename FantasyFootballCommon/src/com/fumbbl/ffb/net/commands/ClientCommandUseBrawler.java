package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUseBrawler extends ClientCommand {

	private int brawlerCount;

	public ClientCommandUseBrawler() {
	}

	public ClientCommandUseBrawler(int brawlerCount) {
		this.brawlerCount = brawlerCount;
	}

	public int getBrawlerCount() {
		return brawlerCount;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_BRAWLER;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.BRAWLER_OPTIONS.addTo(jsonObject, brawlerCount);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		brawlerCount = IJsonOption.BRAWLER_OPTIONS.getFrom(game, UtilJson.toJsonObject(jsonValue));
		return this;
	}
}
