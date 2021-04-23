package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUseBrawler extends ClientCommand {

	private int brawlerCount;
	private String targetId;

	public ClientCommandUseBrawler() {
	}

	public ClientCommandUseBrawler(int brawlerCount, String targetId) {
		this.brawlerCount = brawlerCount;
		this.targetId = targetId;
	}

	public int getBrawlerCount() {
		return brawlerCount;
	}

	public String getTargetId() {
		return targetId;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_BRAWLER;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.BRAWLER_OPTIONS.addTo(jsonObject, brawlerCount);
		IJsonOption.PLAYER_ID.addTo(jsonObject, targetId);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		brawlerCount = IJsonOption.BRAWLER_OPTIONS.getFrom(game, jsonObject);
		targetId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}
}
