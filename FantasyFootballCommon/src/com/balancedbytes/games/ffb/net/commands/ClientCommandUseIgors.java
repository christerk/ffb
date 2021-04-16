package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class ClientCommandUseIgors extends ClientCommand {

	private final List<String> playerIds = new ArrayList<>();

	public ClientCommandUseIgors() { }

	public ClientCommandUseIgors(String playerId) {
		this();
		addPlayerId(playerId);
	}

	public ClientCommandUseIgors(List<String> playerIds) {
		this();
		this.playerIds.addAll(playerIds);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_IGORS;
	}

	public String[] getPlayerIds() {
		return playerIds.toArray(new String[0]);
	}

	public boolean hasPlayerId(String pPlayerId) {
		return playerIds.contains(pPlayerId);
	}

	private void addPlayerId(String pPlayerId) {
		if (StringTool.isProvided(pPlayerId)) {
			playerIds.add(pPlayerId);
		}
	}

	private void addPlayerIds(String[] pPlayerIds) {
		if (ArrayTool.isProvided(pPlayerIds)) {
			for (String playerId : pPlayerIds) {
				addPlayerId(playerId);
			}
		}
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_IDS.addTo(jsonObject, playerIds);
		return jsonObject;
	}

	public ClientCommandUseIgors initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		addPlayerIds(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		return this;
	}

}
