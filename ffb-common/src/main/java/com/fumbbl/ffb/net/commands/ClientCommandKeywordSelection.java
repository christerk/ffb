package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientCommandKeywordSelection extends ClientCommand {
	private String playerId;
	private final List<Keyword> keywords = new ArrayList<>();

	@SuppressWarnings("unused")
	public ClientCommandKeywordSelection() {
	}

	public ClientCommandKeywordSelection(String playerId, List<Keyword> keywords) {
		this.playerId = playerId;
		this.keywords.addAll(keywords);
	}

	public String getPlayerId() {
		return playerId;
	}

	public List<Keyword> getKeywords() {
		return keywords;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_KEYWORD_SELECTION;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.KEYWORDS.addTo(jsonObject, keywords.stream().map(Keyword::getName).collect(Collectors.toList()));
		return jsonObject;
	}

	public ClientCommandKeywordSelection initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		keywords.addAll(Arrays.stream(IJsonOption.KEYWORDS.getFrom(source, jsonObject)).map(Keyword::forName).collect(
			Collectors.toList()));
		return this;
	}

}
